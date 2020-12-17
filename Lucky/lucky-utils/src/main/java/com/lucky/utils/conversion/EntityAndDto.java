package com.lucky.utils.conversion;

import java.util.List;

public class EntityAndDto {

    private LuckyConversion conversion;

    private Class<?> entityClass;

    private Class<?> dtoClass;

    public EntityAndDto(LuckyConversion conversion, Class<?> entityClass, Class<?> dtoClass) {
        this.conversion = conversion;
        this.entityClass = entityClass;
        this.dtoClass = dtoClass;
    }

    public LuckyConversion getConversion() {
        return conversion;
    }

    public void setConversion(LuckyConversion conversion) {
        this.conversion = conversion;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public Class<?> getDtoClass() {
        return dtoClass;
    }

    public void setDtoClass(Class<?> dtoClass) {
        this.dtoClass = dtoClass;
    }

    public static EntityAndDto getEntityAndDtoByDaoClass(List<EntityAndDto> eds, Class<?> dtoClass){
        for(EntityAndDto ed:eds){
            if(dtoClass==ed.getDtoClass())
                return ed;
        }
        return null;
    }

    public static EntityAndDto getEntityAndDtoByEntityClass(List<EntityAndDto> eds,Class<?> entityClass){
        for(EntityAndDto ed:eds){
            if(ed.getEntityClass()==entityClass)
                return ed;
        }
        return null;
    }

}
