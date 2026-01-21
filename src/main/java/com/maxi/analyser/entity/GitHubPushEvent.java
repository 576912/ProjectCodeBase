package com.maxi.analyser.entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubPushEvent {
    private String ref;
    private String before;
    private String after;
    private Repository repository;
    private Pusher pusher;
    private Sender sender;
    private List<Commit> commits;
    private Commit head_commit;

    public String getRef() { return ref; }
    public void setRef(String ref) { this.ref = ref; }
    public String getBefore() { return before; }
    public void setBefore(String before) { this.before = before; }
    public String getAfter() { return after; }
    public void setAfter(String after) { this.after = after; }
    public Repository getRepository() { return repository; }
    public void setRepository(Repository repository) { this.repository = repository; }
    public Pusher getPusher() { return pusher; }
    public void setPusher(Pusher pusher) { this.pusher = pusher; }
    public Sender getSender() { return sender; }
    public void setSender(Sender sender) { this.sender = sender; }
    public List<Commit> getCommits() { return commits; }
    public void setCommits(List<Commit> commits) { this.commits = commits; }
    public Commit getHead_commit() { return head_commit; }
    public void setHead_commit(Commit head_commit) { this.head_commit = head_commit; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Repository {
        private Long id;
        private String name;
        private String full_name;
        private String url;
        private String html_url;
        private Owner owner;
        private Boolean privateRepo;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getFull_name() { return full_name; }
        public void setFull_name(String full_name) { this.full_name = full_name; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getHtml_url() { return html_url; }
        public void setHtml_url(String html_url) { this.html_url = html_url; }
        public Owner getOwner() { return owner; }
        public void setOwner(Owner owner) { this.owner = owner; }
        public Boolean getPrivateRepo() { return privateRepo; }
        // GitHub field name is "private" → handled via this alias setter
        public void setPrivate(Boolean value) { this.privateRepo = value; }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Owner {
            private Long id;
            private String name;
            private String login;
            public Long getId() { return id; }
            public void setId(Long id) { this.id = id; }
            public String getName() { return name; }
            public void setName(String name) { this.name = name; }
            public String getLogin() { return login; }
            public void setLogin(String login) { this.login = login; }
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Pusher {
        private String name;
        private String email;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sender {
        private Long id;
        private String login;
        private String html_url;
        private String avatar_url;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getLogin() { return login; }
        public void setLogin(String login) { this.login = login; }
        public String getHtml_url() { return html_url; }
        public void setHtml_url(String html_url) { this.html_url = html_url; }
        public String getAvatar_url() { return avatar_url; }
        public void setAvatar_url(String avatar_url) { this.avatar_url = avatar_url; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Commit {
        private String id;
        private String message;
        private String timestamp;
        private String url;
        private Author author;
        private Author committer;
        private List<String> added;
        private List<String> removed;
        private List<String> modified;
        private Map<String, Object> tree;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public Author getAuthor() { return author; }
        public void setAuthor(Author author) { this.author = author; }
        public Author getCommitter() { return committer; }
        public void setCommitter(Author committer) { this.committer = committer; }
        public List<String> getAdded() { return added; }
        public void setAdded(List<String> added) { this.added = added; }
        public List<String> getRemoved() { return removed; }
        public void setRemoved(List<String> removed) { this.removed = removed; }
        public List<String> getModified() { return modified; }
        public void setModified(List<String> modified) { this.modified = modified; }
        public Map<String, Object> getTree() { return tree; }
        public void setTree(Map<String, Object> tree) { this.tree = tree; }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Author {
            private String name;
            private String email;
            private String username;
            public String getName() { return name; }
            public void setName(String name) { this.name = name; }
            public String getEmail() { return email; }
            public void setEmail(String email) { this.email = email; }
            public String getUsername() { return username; }
            public void setUsername(String username) { this.username = username; }
        }
    }
}

