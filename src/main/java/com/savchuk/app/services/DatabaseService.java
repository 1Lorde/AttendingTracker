// created by Vlad Savchuk 14.11.2019 22:25
package com.savchuk.app.services;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import com.mongodb.client.model.Filters;
import com.savchuk.app.models.*;

import org.bson.Document;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;

public class DatabaseService {
    private static DatabaseService databaseService;
    private final String USERNAME = "attending_user";
    private final String PASSWORD = "attending_user";
    private final String DB_NAME = "AttendingDB";
    private MongoClient client;
    private MongoDatabase database;
    private MongoCollection<Cadet> cadetsCollection;
    private MongoCollection<User> usersCollection;
    private MongoCollection<Session> sessionsCollection;

    private MongoCollection<Attending> attendingsCollection;

    private DatabaseService() {
    }

    static DatabaseService getInstance() {
        if (databaseService == null) {
            databaseService = new DatabaseService();
        }
        return databaseService;
    }

    synchronized void init() {
        String uri = "mongodb://" + USERNAME + ":" + PASSWORD + "@cluster0-shard-00-00.alm7k.gcp.mongodb.net:27017,cluster0-shard-00-01.alm7k.gcp.mongodb.net:27017,cluster0-shard-00-02.alm7k.gcp.mongodb.net:27017/" + DB_NAME + "?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin&retryWrites=true&w=majority";
        client = MongoClients.create(uri);
        //client = MongoClients.create();
        database = client.getDatabase("AttendingDB");
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );

        database = database.withCodecRegistry(pojoCodecRegistry);
        cadetsCollection = database.getCollection("cadets", Cadet.class);
        usersCollection = database.getCollection("users", User.class);
        sessionsCollection = database.getCollection("sessions", Session.class);
        attendingsCollection = database.getCollection("attendings", Attending.class);
    }


    synchronized void addCadet(Cadet cadet) {
        cadetsCollection.insertOne(cadet);
    }

    synchronized void updateCadet(Cadet cadet) {
        cadetsCollection.replaceOne(Filters.eq("_id", cadet.getId()), cadet);
    }

    synchronized void deleteCadet(Cadet cadet) {
        cadetsCollection.deleteOne(Filters.eq("_id", cadet.getId()));
    }

    synchronized HashMap<ObjectId, Cadet> getCadets() {
        HashMap<ObjectId, Cadet> hashMap = new HashMap<>();
        ArrayList<Cadet> list = cadetsCollection.find(new Document(), Cadet.class).into(new ArrayList<>());

        if (list.isEmpty())
            return null;

        for (Cadet c : list) {
            hashMap.put(c.getId(), c);
        }

        return hashMap;
    }


    synchronized ArrayList<Attending> getAttendings() {
        ArrayList<Attending> list = attendingsCollection.find(new Document(), Attending.class).into(new ArrayList<>());

        if (list.isEmpty())
            return null;

        return list;
    }

    synchronized void addAttending(Attending attending) {
        attendingsCollection.insertOne(attending);
    }

    synchronized void updateAttending(Attending attending) {
        attendingsCollection.replaceOne(Filters.eq("cadetId", attending.getCadetId()), attending);
    }


    synchronized Session auth(User user) {
        Session session;
        if (usersCollection.find(Filters.and(Filters.eq("username", user.getUsername()), Filters.eq("password", user.getPassword()))).first() != null) {
            ObjectId sessionId = new ObjectId();
            Access access = usersCollection.find(Filters.and(Filters.eq("username", user.getUsername()), Filters.eq("password", user.getPassword()))).first().getAccess();
            session = new Session(sessionId, access);
            sessionsCollection.insertOne(session);
            return session;
        }
        return null;
    }

    synchronized void deAuth(ObjectId sessionId) {
        sessionsCollection.deleteOne(Filters.eq("_id", sessionId));
    }

    synchronized Session checkSession(String sessionId) {
        Session session = sessionsCollection.find(Filters.eq("_id", new ObjectId(sessionId))).first();
        if (session != null) {
            return session;
        }

        return null;
    }


}
