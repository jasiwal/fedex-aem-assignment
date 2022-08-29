package com.aem.fedex.core.models;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(AemContextExtension.class)
public class RSSFeedModelTest {

    private static final String CONTENT_PATH = "/content/fedex/us/en/rss-feed";
    private static final String JSON_FILE_PATH = "/com/fedex/pages/rssFeed.json";

    private final AemContext aemContext = new AemContext(ResourceResolverType.RESOURCERESOLVER_MOCK);
    private RSSFeedModel rssFeedModel;

    @BeforeEach
    void setUp() throws Exception {

        aemContext.load().json(JSON_FILE_PATH, "/content/fedex/us/en");
        aemContext.addModelsForClasses(RSSFeedModel.class);

        aemContext.currentPage(CONTENT_PATH);
        aemContext.currentResource(CONTENT_PATH + "/jcr:content/root/container/container/rssfeed");
        rssFeedModel = Objects.requireNonNull(aemContext.request().adaptTo(RSSFeedModel.class));
    }

    @Test
    void testGetContent() {
        assertEquals(5, Objects.requireNonNull(rssFeedModel).getRssFeeds().size());
        RSSFeedModel.RSSFeed rssFeed = rssFeedModel.getRssFeeds().stream().findFirst().get();
        assertNotNull(rssFeed.getTitle());
        assertNotNull(rssFeed.getDescription());
        assertNotNull(rssFeed.getPublishDate());
    }

}
