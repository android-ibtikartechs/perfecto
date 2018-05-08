package com.perfecto.apps.ocr.models;

/**
 * Created by Hosam Azzam on 20/08/2017.
 */

public class File {
    String id, name, docid, date, trans, desc, docname, sourceLangPos;

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDocid() {
        return docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }

    public String getTrans() {
        return trans;
    }

    public void setTrans(String trans) {
        this.trans = trans;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDocname() {
        return docname;
    }

    public void setDocname(String docname) {
        this.docname = docname;
    }

    public String getSourceLangPos() {
        return sourceLangPos;
    }

    public void setSourceLangPos(String sourceLang) {
        this.sourceLangPos = sourceLang;
    }
}
