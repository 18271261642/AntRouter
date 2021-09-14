package com.jkcq.antrouter.bean;

import com.jkcq.antrouter.okhttp.BaseBean;

/*
 *
 *
 * @author mhj
 * Create at 2019/2/21 18:17
 */public class ClubInfo extends BaseBean{

    /**
     * id : 10007
     * name : 我的会所7
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
        return "ClubInfo{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
