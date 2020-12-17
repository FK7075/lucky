package com.lucky.utils.conversion;

/**
 * Entity与Dto相互转化的泛型接口
 * @param <E> Entity
 * @param <D> Dto
 */
public interface LuckyConversion<E,D> {

    /**
     * Dao转为Entity
     * @param dto Dto对象
     * @return Entity对象
     */
     E toEntity(D dto);

    /**
     * Entity转为Dao
     * @param entity Entity对象
     * @return Dao对象
     */
     D toDto(E entity);


}
