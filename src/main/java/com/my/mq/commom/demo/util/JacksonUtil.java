package com.my.mq.commom.demo.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * bean转json格式或者json转bean格式, 项目中我们通常使用这个工具类进行json---java互相转化
 *
 *
 */
@Slf4j
public final class JacksonUtil {
    /**
     * Jackson序列化工具对象
     */
    private static ObjectMapper MAPPER = new ObjectMapper();

    private JacksonUtil() {
    }

    static {
        // 允许出现特殊字符和转义符
        MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 统一序列化规则，将LONG转换为字符串
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        MAPPER.registerModule(simpleModule);
    }

    /**
     * 将对象序列化为字符串。
     *
     * @param obj java对象
     * @return 序列化字符串。如果序列化失败，则返回NULL
     */
    public static String bean2Json(Object obj) {
        if (obj == null) {
            return null;
        }

        if (obj instanceof String) {
            return (String) obj;
        }

        try {
            return MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("序列化失败:{}, 错误:{}", obj, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 反序列化字符串为指定对象实例
     *
     * @param jsonString json字符串
     * @param clazz      对象类
     * @param <T>        范型
     * @return 如果反序列化成功，则返回java bean对象，否则返回NULL
     */
    public static <T> T json2Bean(String jsonString, Class<T> clazz) {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }

        try {
            return MAPPER.readValue(jsonString, clazz);
        } catch (IOException e) {
            log.error("反序列化失败: {}, {}, 错误: {}",
                    jsonString, clazz.getSimpleName(), e.getMessage(), e);
            return null;
        }
    }

    /**
     * 反序列化字符串为指定对象实例。
     * 该API如果处理失败，会抛出异常。
     *
     * @param jsonString json字符串
     * @param clazz      对象类
     * @param <T>        范型
     * @return 对象反序列化实例。如果字符串为空，则返回NULL。
     * @throws IOException 反序列化异常
     */
    public static <T> T json2BeanWithEx(String jsonString, Class<T> clazz)
            throws IOException {
        return MAPPER.readValue(jsonString, clazz);
    }

    /**
     * 反序列化复杂Collection如List<Bean>, 先使用createCollectionType构造类型,然后调用本函数.
     *
     * @param jsonString json序列化字符串
     * @param javaType   java bean 类型
     * @param <T>        范型
     * @return 如果反序列化成功，则返回java bean对象，否则返回NULL
     * @see #constructCollectionType(Class, Class...)
     */
    public static <T> T json2Bean(String jsonString, JavaType javaType) {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }

        try {
            return (T) MAPPER.readValue(jsonString, javaType);
        } catch (IOException e) {
            log.error("反序列化失败: {}, {}, 错误: {}",
                    jsonString, javaType.getTypeName(), e.getMessage(), e);
            return null;
        }
    }

    /**
     * 构造的Collection Type如:
     * ArrayList<Bean>, 则调用constructCollectionType(ArrayList.class,Bean.class)
     * HashMap<String,Bean>, 则调用constructCollectionType(HashMap.class,String.class, Bean.class)
     *
     * @param collectionClass 集合类
     * @param elementClasses  集合内元素类
     * @return 集合类的java 类型
     */
    public static JavaType constructCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return MAPPER.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    /**
     * 反序列化字符串成为对象
     *
     * @param jsonString   json字符串
     * @param valueTypeRef 值类型引用
     * @param <T>          范型
     * @return 如果反序列化成功，则返回java bean对象，否则返回NULL
     */
    public static <T> T json2Bean(String jsonString, TypeReference<T> valueTypeRef) {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }

        try {
            return MAPPER.readValue(jsonString, valueTypeRef);
        } catch (IOException e) {
            log.error("反序列化失败: {}, {}, 错误: {}",
                    jsonString, valueTypeRef.getType().getTypeName(), e.getMessage(), e);
            return null;
        }
    }

    /**
     * 反序列化字符串成为对象。
     * 该API如果处理失败，会抛出异常。
     *
     * @param jsonString   json字符串
     * @param valueTypeRef 值类型引用
     * @param <T>          范型
     * @return 对象反序列化实例。
     * @throws IOException 反序列化异常
     */
    public static <T> T json2BeanWithEx(String jsonString, TypeReference<T> valueTypeRef)
            throws IOException {
        return MAPPER.readValue(jsonString, valueTypeRef);
    }

    /**
     * 将json字符串转换为包含多个java bean值的map对象
     *
     * @param jsonString json序列化字符串
     * @param clazz      map中的value类
     * @param <T>        value类的范型
     * @return 如果反序列化成功，则返回map对象，否则返回NULL
     */
    public static <T> Map<String, T> json2Map(String jsonString, Class<T> clazz) {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }

        try {
            Map<String, Map<String, Object>> map = MAPPER.readValue(jsonString,
                    new TypeReference<Map<String, T>>() {
                    });
            Map<String, T> result = new HashMap<>();
            for (Map.Entry<String, Map<String, Object>> entry : map.entrySet()) {
                result.put(entry.getKey(), map2Bean(entry.getValue(), clazz));
            }
            return result;
        } catch (Exception e) {
            log.error("反序列化失败: {}, 错误: {}", jsonString, e.getMessage(), e);
            return null;
        }
    }


    /**
     * 将map对象转换为java Bean对象
     *
     * @param map   map对象
     * @param clazz 目标java类
     * @param <T>   类范型
     * @return 转换后的java bean实例
     */
    public static <T> T map2Bean(Map map, Class<T> clazz) {
        if (map == null) {
            log.debug("map对象为空: {}", clazz);
            return null;
        }

        return MAPPER.convertValue(map, clazz);
    }

}
