package me.biiee3l.bconfig.config.types;

import com.mongodb.client.MongoCollection;
import me.biiee3l.bconfig.config.Configuration;
import org.bson.Document;

public class MongoConfiguration extends Configuration {

    private final Document query;
    private final MongoCollection<Document> collection;

    public MongoConfiguration(Document query, MongoCollection<Document> collection){
        this.query = query;
        this.collection = collection;
    }

    @Override
    public void save() {
        Document document = new Document(config);
        collection.deleteMany(document);
        collection.insertOne(document);
    }

    @Override
    public boolean load() {
        Document document = collection.find(query).first();
        if(document != null){
            config = document;
        }else {
            config = query;
        }
        return true;
    }
}
