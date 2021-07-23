package com.leijendary.spring.iamtemplate.factory;

import org.modelmapper.ModelMapper;

import static com.leijendary.spring.iamtemplate.util.SpringContext.getBean;

public abstract class AbstractFactory {

    protected static final ModelMapper MAPPER = getBean(ModelMapper.class);
}
