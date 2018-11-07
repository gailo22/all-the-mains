package com.gailo22;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.reflections.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Set;


public class Main77 {
	
	public static void main(String[] args) {

        ObjectMapper objectMapper = new ObjectMapper();

        AccountAddress t = new AccountAddress();
        t.setAddressType("");
        AccountAddressStandard accountAddressStandard = new AccountAddressStandard();
        accountAddressStandard.setAddressZipCode("0111");
        accountAddressStandard.setAddressMoo("");
        t.setAddressStandard(accountAddressStandard);
        setEmptyStringToNull(t);

        try {
            String s = objectMapper.writeValueAsString(t);
            System.out.println(s);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


    static <T> void setEmptyStringToNull(T t) {
        Set<Field> fields = ReflectionUtils.getFields(t.getClass());
        fields.forEach(it -> {
            it.setAccessible(true);
            try {
                Object t1 = it.get(t);
                if (t1 == null) return;

                if (it.getType().isAssignableFrom(String.class)) {
                    String value = (String) t1;
                    if (StringUtils.isBlank(value)) {
                        it.set(t, null);
                    }
                } else {
                    setEmptyStringToNull(t1);
                }
            } catch (IllegalAccessException ignore) {
            }
        });
    }

}

@Data
class AccountAddress {
    private String addressType;
    private AccountAddressStandard addressStandard;
}

@Data
class AccountAddressStandard {
    private String addressZipCode;
    private String addressMoo;
}
