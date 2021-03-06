package me.kcaldwell.hackernewsreader.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmResults;

public class FeedItemDao {

    private static final String TAG = FeedItemDao.class.getSimpleName();

    public static void createOrUpdateFeedItemsFromArray(JSONArray feed, Realm realmInstance) {
        final FeedItem feedItem = new FeedItem();
        realmInstance.executeTransaction(realm -> {
            for (int i = 0; i < feed.length(); i++) {
                try {
                    JSONObject article = feed.getJSONObject(i);
                    feedItem.setId(article.getLong("id"));
                    feedItem.setTitle(article.getString("title"));
                    if (!article.isNull("points") && article.has("points")) {
                        feedItem.setPoints(article.getInt("points"));
                    } else {
                        feedItem.setPoints(0);
                    }
                    if (!article.isNull("user") && article.has("user")) {
                        feedItem.setAuthor(article.getString("user"));
                    } else {
                        feedItem.setAuthor("Deleted User");
                    }
                    feedItem.setTime(article.getLong("time"));
                    feedItem.setTimeAgo(article.getString("time_ago"));
                    feedItem.setCommentsCount(article.getInt("comments_count"));
                    feedItem.setType(article.getString("type"));
                    if (!article.isNull("url") && article.has("url")) {
                        feedItem.setUrl(article.getString("url"));
                    } else {
                        feedItem.setUrl("");
                    }
                    if (!article.isNull("domain") && article.has("domain")) {
                        feedItem.setDomain(article.getString("domain"));
                    } else {
                        feedItem.setDomain("");
                    }
                    realmInstance.copyToRealmOrUpdate(feedItem);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static FeedItem getFeedItemById(long id, Realm realmInstance) {
        return realmInstance.where(FeedItem.class).equalTo("id", id).findFirst();
    }

    public static RealmResults<FeedItem> getAllFeedItems(Realm realmInstance) {
        return realmInstance.where(FeedItem.class).findAll();
    }

    public static void removeStaleFeedItems(Realm realmInstance) {
        long oneHourAgo = 3600000L;
        long now = System.currentTimeMillis();
        long yesterday = now - oneHourAgo;

        RealmResults<FeedItem> staleItems = realmInstance.where(FeedItem.class)
                .lessThan("time", yesterday)
                .findAll();
        if (staleItems.size() > 0) {
            realmInstance.executeTransaction(realm -> staleItems.deleteAllFromRealm());
        }
    }

    public static void deleteAllFeedItems(Realm realmInstance) {
        RealmResults<FeedItem> staleItems = realmInstance.where(FeedItem.class)
                .findAll();
        if (staleItems.size() > 0) {
            realmInstance.executeTransaction(realm -> staleItems.deleteAllFromRealm());
        }
    }

}
