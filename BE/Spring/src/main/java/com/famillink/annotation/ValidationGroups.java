package com.famillink.annotation;


import net.bytebuddy.implementation.bind.annotation.Default;

public class ValidationGroups {
    private ValidationGroups() {
    }

    public interface signup extends Default {};

    public interface login extends Default {};

    public interface find_password extends Default {};

    public interface mail extends Default {};

    public interface date extends Default {};

    public interface regist extends Default {};




}
