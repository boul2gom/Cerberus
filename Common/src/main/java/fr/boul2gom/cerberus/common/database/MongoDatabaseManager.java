package fr.boul2gom.cerberus.common.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import fr.boul2gom.cerberus.api.CerberusAPI;
import fr.boul2gom.cerberus.api.database.IDatabaseManager;
import fr.boul2gom.cerberus.api.database.Storable;
import fr.boul2gom.cerberus.common.support.database.MongoSupport;
import org.bson.Document;
import org.bson.conversions.Bson;

public class MongoDatabaseManager implements IDatabaseManager {

    private final MongoSupport support;

    public MongoDatabaseManager(MongoSupport support) {
        this.support = support;
    }

    @Override
    public <T extends Storable> T fetch(String folder, String key, Class<T> clazz) {
        final MongoCollection<Document> collection = this.support.getCollection(folder);
        final Document document = collection.find(Filters.eq("_id", key)).first();

        if (document == null) {
            return null;
        }

        return CerberusAPI.get().getGson().fromJson(document.toJson(), clazz);
    }

    @Override
    public <T extends Storable> void save(String folder, String key, T value) {
        CerberusAPI.get().getScheduler().async(() -> {
            final MongoCollection<Document> collection = this.support.getCollection(folder);
            final Document document = Document.parse(CerberusAPI.get().getGson().toJson(value));
            document.put("_id", key);

            // Replace the document if it already exists, otherwise insert it
            final Bson filters = Filters.eq("_id", key);
            final ReplaceOptions options = new ReplaceOptions().upsert(true);

            collection.replaceOne(filters, document, options);
        });
    }

    @Override
    public void delete(String folder, String key) {
        CerberusAPI.get().getScheduler().async(() -> {
            final MongoCollection<Document> collection = this.support.getCollection(folder);
            collection.deleteOne(Filters.eq("_id", key));
        });
    }
}
