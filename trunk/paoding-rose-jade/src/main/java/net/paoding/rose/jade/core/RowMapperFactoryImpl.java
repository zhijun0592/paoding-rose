/*
 * Copyright 2009-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License i distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.paoding.rose.jade.jadeinterface.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.paoding.rose.jade.jadeinterface.impl.mapper.ArrayRowMapper;
import net.paoding.rose.jade.jadeinterface.impl.mapper.ListRowMapper;
import net.paoding.rose.jade.jadeinterface.impl.mapper.MapEntryColumnRowMapper;
import net.paoding.rose.jade.jadeinterface.impl.mapper.MapEntryRowMapper;
import net.paoding.rose.jade.jadeinterface.impl.mapper.SetRowMapper;
import net.paoding.rose.jade.jadeinterface.provider.Modifier;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;

/**
 * 支持DAO方法返回类型：
 * <p>
 * <ul>
 * <li>int/Integer、long/Long等primitive&wrapper：期望返回单列，0行或1行</li>
 * <li>String、BigDecimal：期望返回单列，0行或1行</li>
 * <li>java.util.Date及其子类：期望返回单列，0行或1行</li>
 * <li>byte[]：期望返回单列，0行或1行</li>
 * <li>Blob、Clob：期望返回单，0行或1行</li>
 * <li><code>数组(String[]、User[]等)：期望返回单列，多行；</li>
 * <li>数组(User[]等)：期望返回多列，多行；</li>
 * <li>集合(List<;Integer>、Set<String>等): 期望返回单列，多行；</li>
 * <li>集合(List<User>、Set<User>等): 期望返回单列，多行；</li>
 * <li>映射(Map<String, Date>): 期望返回2列，多行</li>
 * <li>映射(Map<String, User>): 期望返回多列，多行</li>
 * <li>映射(Map<String, String[]>): 期望返回多列，多行</li>
 * <ul>
 * TODO: 将以上的尖括号变为HTML可识别
 * 
 * @author 王志亮 [qieqie.wang@gmail.com]
 * @author 廖涵 [in355hz@gmail.com]
 */
public class RowMapperFactoryImpl implements RowMapperFactory {

    private static Log logger = LogFactory.getLog(RowMapperFactory.class);

    @Override
    public RowMapper getRowMapper(Modifier modifier) {

        Class<?> returnClassType = modifier.getReturnType();
        Class<?> rowType = getRowType(modifier);

        // BUGFIX: SingleColumnRowMapper 处理  Primitive Type 抛异常
        if (rowType.isPrimitive()) {
            rowType = ClassUtils.primitiveToWrapper(rowType);
        }

        // 根据类型创建  RowMapper
        RowMapper rowMapper;

        // 返回单列的查询的(或者返回只有2列的Map类型查询的)
        if (TypeUtils.isColumnType(rowType)) {
            if (returnClassType == Map.class) {
                rowMapper = new MapEntryColumnRowMapper(modifier, rowType);
            } else {
                rowMapper = new SingleColumnRowMapper(rowType);
            }
        }
        // 返回多列的，用Bean对象、集合、映射、数组来表示每一行的
        else {
            if (rowType == Map.class) {
                rowMapper = new ColumnMapRowMapper();
            } else if (rowType.isArray()) {
                rowMapper = new ArrayRowMapper(rowType);
            } else if ((rowType == List.class) || (rowType == Collection.class)) {
                rowMapper = new ListRowMapper(modifier);
            } else if (rowType == Set.class) {
                rowMapper = new SetRowMapper(modifier);
            } else {
                rowMapper = new BeanPropertyRowMapper(rowType);
            }
            // 如果DAO方法最终返回的是Map，rowMapper要返回Map.Entry对象
            if (returnClassType == Map.class) {
                rowMapper = new MapEntryRowMapper(modifier, rowMapper);
            }
        }

        if (logger.isInfoEnabled()) {
            logger.info("using rowMapper " + rowMapper + " for " + modifier);
        }

        return rowMapper;
    }

    // 获得返回的集合元素类型
    private static Class<?> getRowType(Modifier modifier) {
        Class<?> returnClassType = modifier.getReturnType();
        if (Collection.class.isAssignableFrom(returnClassType)) {
            return getRowTypeFromCollectionType(modifier, returnClassType);
        } else if (Map.class == returnClassType) {
            return getRowTypeFromMapType(modifier, returnClassType);
        } else if (returnClassType.isArray() && returnClassType.getComponentType() != byte[].class) {
            // 数组类型, 支持多重数组
            return returnClassType.getComponentType();
        }

        // 此时代表整个DAO方法只关心结果集第一行
        return returnClassType;
    }

    private static Class<?> getRowTypeFromMapType(Modifier modifier, Class<?> returnClassType) {
        Class<?> rowType;
        // 获取  Map<K, V> 值元素类型
        Class<?>[] genericTypes = modifier.getGenericReturnTypes();
        if (genericTypes.length != 2) {
            throw new IllegalArgumentException("the returned generic type '"
                    + returnClassType.getName() + "' should has two actual type parameters.");
        }
        rowType = genericTypes[1]; // 取  V 类型
        return rowType;
    }

    private static Class<?> getRowTypeFromCollectionType(Modifier modifier, Class<?> returnClassType) {
        Class<?> rowType;
        // 仅支持  List / Collection / Set
        if ((returnClassType != List.class) && (returnClassType != Collection.class)
                && (returnClassType != Set.class)) {
            throw new IllegalArgumentException("error collection type " + returnClassType.getName()
                    + "; only support List, Set, Collection");
        }
        // 获取集合元素类型
        Class<?>[] genericTypes = modifier.getGenericReturnTypes();
        if (genericTypes.length != 1) {
            throw new IllegalArgumentException("the returned generic type '"
                    + returnClassType.getName() + "' should has a actual type parameter.");
        }
        rowType = genericTypes[0];
        return rowType;
    }

}