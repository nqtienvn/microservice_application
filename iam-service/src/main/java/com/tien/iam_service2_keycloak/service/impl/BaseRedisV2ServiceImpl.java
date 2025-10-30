package com.tien.iam_service2_keycloak.service.impl;

import com.tien.iam_service2_keycloak.service.BaseRedisV2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class BaseRedisV2ServiceImpl implements BaseRedisV2Service {
    private final RedisTemplate<String, Object> redisTemplate; //thao tác với key value
    private final HashOperations<String, String, Object> hashOperations; //thao tác với thắn key value object

    @Override
    public void set(String key, String value) {
        //save redis
        redisTemplate.opsForValue().set(key, value);
    }


    //set thoi gian song
    @Override
    public void setTimeToLive(String key, long timeoutInDays) {
        redisTemplate.expire(key, timeoutInDays, TimeUnit.SECONDS);
    }
    //save to redis theo kieu la hash

    /**
     * @param key:   like table name
     * @param field: like field name table
     * @param value: like value of field name in table
     */
    @Override
    public void hashSet(String key, String field, Object value) {
        hashOperations.put(key, field, value);
    }

    /**
     * @param key:        ten bang
     * @param field:check field exists in hash table
     * @return: boolean
     */
    @Override
    public boolean hashExists(String key, String field) {
        return hashOperations.hasKey(key, field);
    }

    /**
     * @param key: lay ve value cua key
     * @return: tra value truc tiep dang String nha
     * redis template luu all la STRING
     */
    @Override
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * @param key: dinh nghia bang key
     * @return: lay toan bo cac data trong bang
     */
    @Override
    public Map<String, Object> getField(String key) {
        return hashOperations.entries(key);
    }

    /**
     * @param key:   ten bang
     * @param field: ten cot
     * @return: tra ve 1 o data
     * tim theo field
     */
    @Override
    public Object hashGet(String key, String field) {
        return hashOperations.get(key, field);
    }

    /**
     * lay value trong bang, nhung cot bat dau bang tien to prefix
     *
     * @param key:         ten bang
     * @param filedPrefix: startWith
     * @return: return ve list objects
     */
    @Override
    public List<Object> hashGetByFieldPrefix(String key, String filedPrefix) {
        List<Object> objects = new ArrayList<>();
        Map<String, Object> hashEntries = hashOperations.entries(key);
        for (Map.Entry<String, Object> entry : hashEntries.entrySet()) {
            if (entry.getKey().startsWith(filedPrefix)) {
                objects.add(entry.getValue());
            }
        }
        return objects;
    }

    /**
     * lay danh sach ten cot trong bang
     *
     * @param key: ten bang
     * @return: tra ve het cac cot trong bang
     */
    @Override
    public Set<String> getFieldPrefixes(String key) {
        return hashOperations.entries(key).keySet();
    }

    //xoa theo key
    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * xoa 1 cot trong bang
     *
     * @param key:
     * @param field:
     */
    @Override
    public void delete(String key, String field) {
        hashOperations.delete(key, field);
    }

    /**
     * xoa nhieu cot trong bang
     *
     * @param key:
     * @param fields:
     */
    @Override
    public void delete(String key, List<String> fields) {
        for (String field : fields) {
            hashOperations.delete(key, fields);
        }
    }
}
