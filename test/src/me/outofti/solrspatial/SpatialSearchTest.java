package me.outofti.solrspatial;

import org.junit.Test;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;

public class SpatialSearchTest extends TestHelper {
    private static String STANDARD_LAT_FIELD = "lat";
    private static String STANDARD_LNG_FIELD = "lng";
    private static String PARAM_NAME = "spatial";

    @Test public void simpleSpatialSearch() throws Exception {
        addStandardFixtures();
        final SolrQuery query = new SolrQuery();
        query.add(PARAM_NAME, "{!radius=10}40.7142691, -74.0059729");
        assertResults(query, "New York", "Brooklyn");
    }

    @Test public void customFieldNames() throws Exception {
        addStandardFixtures("latitude", "longitude");
        final SolrQuery query = new SolrQuery();
        query.add("spatial",
                  "{!radius=10}latitude:40.7142691, longitude:-74.0059729");
        assertResults(query, "New York", "Brooklyn");
    }

    @Test public void simpleDistanceSorting() throws Exception {
        addStandardFixtures();
        final SolrQuery query = new SolrQuery();
        query.add("spatial", "{!sort=true}40.7142691, -74.0059729");
        assertResultsInOrder(query, "New York", "Brooklyn", "Staten Island",
                             "Yonkers");
    }

    @Test public void specifiedLimit() throws Exception {
        addStandardFixtures();
        final SolrQuery query = new SolrQuery();
        query.add("spatial", "{!sort=true}40.7142691, -74.0059729");
        query.setRows(2);
        assertResultsInOrder(query, "New York", "Brooklyn");
    }

    @Test public void compoundSort() throws Exception {
        addStandardFixtures();
        final SolrQuery query = new SolrQuery();
        query.add("spatial", "{!sort=true}40.7142691, -74.0059729");
        query.addSortField("rating", SolrQuery.ORDER.desc);
        assertResultsInOrder(query, "Brooklyn", "New York", "Staten Island",
                             "Yonkers");
    }

    private void addStandardFixtures() throws Exception {
        addStandardFixtures(STANDARD_LAT_FIELD, STANDARD_LNG_FIELD);
    }

    private void addStandardFixtures(String latField, String lngField) throws Exception {
        // 11.765 miles, but inside the bounding box
        addLocation("Staten Island", latField, lngField, 4.0, 40.5834379, -74.1495875);
        // 0.000 miles
        addLocation("New York", latField, lngField, 4.0, 40.7142691, -74.0059729);
        // 16.000 miles
        addLocation("Yonkers", latField, lngField, 4.0, 40.9312099, -73.8987469);
        // 5.328 miles
        addLocation("Brooklyn", latField, lngField, 5.0, 40.6501037, -73.9495823);
        getServer().commit();
    }
}
