package com.aem.fedex.core.models;

import com.day.cq.wcm.api.Page;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Model(
        adaptables = {SlingHttpServletRequest.class, Resource.class},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
        resourceType = "fedex/components/rssfeed")
public class RSSFeedModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(RSSFeedModel.class);
    @ChildResource(name = "rssFeeds")
    Collection<RSSFeedModel.RSSFeed> rssFeeds;
    @Inject
    private Page currentPage;
    @Self
    private SlingHttpServletRequest request;
    @Inject
    @Via(value = "resource")
    private int noOfItems;

    @PostConstruct
    protected void init() {
        List<RSSFeedModel.RSSFeed> liveEnteries = new ArrayList<>();
        try {
            URL feedSource = new URL("https://sports.ndtv.com/rss/cricket");
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedSource));

            feed.getEntries().subList(0, noOfItems).forEach(eachFeed -> {
                RSSFeedModel.RSSFeed eachLiveFeed = new RSSFeedModel.RSSFeed();
                SyndEntry syndFeed = (SyndEntry) eachFeed;
                eachLiveFeed.setTitle(syndFeed.getTitle());
                eachLiveFeed.setDescription(syndFeed.getDescription().getValue());
                String pubDate = ((ArrayList<Element>) syndFeed.getForeignMarkup()).get(0).getContent().get(0).toString();
                eachLiveFeed.setPublishDate(StringUtils.substringBetween(pubDate, "[Text: ", "T"));
                liveEnteries.add(eachLiveFeed);
            });

            if (!liveEnteries.isEmpty()) {
                rssFeeds = liveEnteries;
            }


        } catch (IOException | FeedException e) {
            LOGGER.error("Error while parsing the RSS feed", e);
        }

    }


    public Collection<RSSFeedModel.RSSFeed> getRssFeeds() {
        return rssFeeds;
    }

    @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
    public static class RSSFeed {
        @Inject
        private String title;
        @Inject
        private String description;
        @Inject
        private String publishDate;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getPublishDate() {
            return publishDate;
        }

        public void setPublishDate(String publishDate) {
            this.publishDate = publishDate;
        }
    }
}
