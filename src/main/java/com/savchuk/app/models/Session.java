// created by Vlad Savchuk 26.11.2019 3:17
package com.savchuk.app.models;

import org.bson.types.ObjectId;

public class Session {
    private ObjectId id;
    private Access access;

    public Session() {
    }

    public Session(ObjectId id, Access access) {
        this.id = id;
        this.access = access;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }
}
