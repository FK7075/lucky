package com.lucky.jacklamb.jdbc.core.sql;

import com.lucky.jacklamb.annotation.table.Id;
import com.lucky.jacklamb.enums.PrimaryType;
import com.lucky.jacklamb.jdbc.potable.PojoManage;
import com.lucky.utils.reflect.ClassUtils;
import com.lucky.utils.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BatchInsert {

    private String insertSql;

    private Object[] insertObject;

    private int size;

    private String dbname;

    public String getInsertSql() {
        return insertSql;
    }

    public Object[] getInsertObject() {
        return insertObject;
    }

    public <T> BatchInsert(Collection<T> collection,String dbname) {
        size = collection.size();
        this.dbname=dbname;
        if (!collection.isEmpty()) {
            Class<?> pojoClass = null;
            for (T t : collection) {
                pojoClass = t.getClass();
                break;
            }
            insertSql = createInsertSql(pojoClass, collection.size());
            insertObject = createInsertObject(collection);
        }
    }

    public String createInsertSql(Class<?> clzz, int size) {
        Field[] fields = ClassUtils.getAllFields(clzz);
        StringBuilder prefix = new StringBuilder("INSERT INTO `" + PojoManage.getTable(clzz,dbname)+"`");
        StringBuilder suffix = new StringBuilder(" VALUES ");
        boolean isFirst = true;
        List<Field> list;
        if (PojoManage.getIdType(clzz,dbname) == PrimaryType.AUTO_INT) {
            String id = PojoManage.getIdString(clzz,dbname);
            list = Stream.of(fields).filter(field -> !id.equals(PojoManage.getTableField(dbname,field))
                    && FieldUtils.isJDKType(field)
                    && !FieldUtils.isParentClass(field,Collection.class)).collect(Collectors.toList());
        } else {
            list = Stream.of(fields).filter(field -> FieldUtils.isJDKType(field)
                    && !FieldUtils.isParentClass(field,Collection.class)).collect(Collectors.toList());
        }
        StringBuilder fk = new StringBuilder();
        for (Field field : list) {
            if (PojoManage.isNoColumn(field, dbname)) {
                continue;
            }
            if (isFirst) {
                isFirst = false;
                prefix.append("(").append("`").append(PojoManage.getTableField(dbname, field)).append("`").append(",");
                fk.append("(?,");
            } else {
                prefix.append("`").append(PojoManage.getTableField(dbname, field)).append("`").append(",");
                fk.append("?,");
            }
        }
        fk = new StringBuilder(fk.substring(0, fk.length() - 1)).append(")");
        prefix = new StringBuilder(prefix.substring(0, prefix.length() - 1)).append(")");
        String suf=copyRepeatStr(fk.append(","),size);
        return prefix.append(suffix.append(suf, 0, suf.length()-1)).toString();
    }

    private String copyRepeatStr(StringBuilder str,int frequency){
        if(frequency==0)
            return " ";
        StringBuilder sb=new StringBuilder();
        StringBuilder mid=new StringBuilder();
        int base = (int) Math.floor(Math.sqrt(frequency));
        for (int i=0;i<base;i++){
            mid.append(str);
        }
        int residue=frequency%base;
        int result=frequency/base;
        for(int i=0;i<result;i++){
            sb.append(mid);
        }

        for(int i=0;i<residue;i++){
            sb.append(str);
        }
        return sb.toString();
    }

    private <T> Object[] createInsertObject(Collection<T> collection) {
        List<Object> po = new ArrayList<>();
        for (T t : collection) {
            Class<?> clzz = t.getClass();
            Field[] fields = ClassUtils.getAllFields(clzz);
            for (Field field : fields) {
                if(PojoManage.isNoColumn(field,dbname)){
                    continue;
                }
                if(field.isAnnotationPresent(Id.class)){
                    if(PrimaryType.AUTO_INT==field.getAnnotation(Id.class).type()){
                        continue;
                    }else{
                        po.add(FieldUtils.getValue(t, field));
                    }
                }else{
                    if (FieldUtils.isJDKType(field)&& !FieldUtils.isParentClass(field,Collection.class)) {
                        po.add(FieldUtils.getValue(t, field));
                    }
                }
            }
        }
        return po.toArray();
    }

    public String singleInsertSql() {
        String insertSql = getInsertSql();
        int end = insertSql.indexOf("?)") + 2;
        return insertSql.substring(6, end) + " ";
    }

    public String OrcaleInsetSql() {
        StringBuilder insert = new StringBuilder("INSERT ALL");
        String singleSql = singleInsertSql();
        for (int i = 0; i < size; i++) {
            insert.append(singleSql);
        }
        insert.append("SELECT * FROM DUAL");
        return insert.toString();
    }

    public static void main(String[] args) {
        List<Book> list = new ArrayList<>();
        list.add(new Book(1, "b1", 23.4));
        list.add(new Book(2, null, 28.8));
        list.add(new Book(3, "b3", null));
        BatchInsert bi = new BatchInsert(list,"defaultDb");
        String insertSql2 = bi.getInsertSql();
        System.out.println(insertSql2);
        System.out.println(Arrays.toString(bi.getInsertObject()));
        for (Object o : bi.insertObject) {
            System.out.println(o==null);
        }
        System.out.println(bi.OrcaleInsetSql());
    }

}

class Book {
    @Id(type = PrimaryType.AUTO_INT)
    private Integer bid;
    private String bname;
    private Double price;


    public Book(Integer bid, String bname, Double price) {
        this.bid = bid;
        this.bname = bname;
        this.price = price;
    }

    public Integer getBid() {
        return bid;
    }

    public void setBid(Integer bid) {
        this.bid = bid;
    }

    public String getBname() {
        return bname;
    }

    public void setBname(String bname) {
        this.bname = bname;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }


}
