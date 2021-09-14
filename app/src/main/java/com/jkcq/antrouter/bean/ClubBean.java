package com.jkcq.antrouter.bean;

import com.jkcq.antrouter.okhttp.BaseBean;

import java.util.List;

/*
 *
 *
 * @author mhj
 * Create at 2019/2/18 14:19
 */public class ClubBean extends BaseBean {


    private List<ListBean> obj;

    public List<ListBean> getList() {
        return obj;
    }

    public void setList(List<ListBean> list) {
        this.obj = list;
    }

    public static class ListBean {
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
    }
}
