package com.jkcq.antrouter.bean;

/**
 * created by wq on 2019/5/17
 */

public  class BrandInfo {

    /**
     * id : 1115582115157766146
     * name : 健身康复中心
     */

    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "DeviceTypeInfo{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}