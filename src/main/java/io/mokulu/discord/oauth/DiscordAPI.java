package io.mokulu.discord.oauth;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.mokulu.discord.oauth.model.Connection;
import io.mokulu.discord.oauth.model.Guild;
import io.mokulu.discord.oauth.model.Member;
import io.mokulu.discord.oauth.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class DiscordAPI
{
    public static final String BASE_URI = "https://discord.com/api";
    private static final Gson gson = new GsonBuilder().serializeNulls().enableComplexMapKeySerialization().create();
    private final String accessToken;

    private static <T> T toObject(String str, Class<T> clazz)
    {
        return gson.fromJson(str, clazz);
    }

    private String getVersion() throws IOException
    {
        InputStream resourceAsStream = this.getClass().getResourceAsStream("/version.properties");
        java.util.Properties prop = new java.util.Properties();
        prop.load(resourceAsStream);
        return prop.getProperty("version");
    }

    private void setHeaders(org.jsoup.Connection request) throws IOException
    {
        request.header("Authorization", "Bearer " + accessToken);
        request.header("User-Agent",
            String.format("Mokulu-Discord-OAuth2-Java, version %s, platform %s %s", getVersion(), System.getProperty("os.name"),
                System.getProperty("os.version")));
    }

    private String handleGet(String path) throws IOException
    {
        org.jsoup.Connection request = Jsoup.connect(BASE_URI + path).ignoreContentType(true);
        setHeaders(request);

        return request.get().body().text();
    }

    public User fetchUser() throws IOException
    {
        return toObject(handleGet("/users/@me"), User.class);
    }

    public List<Guild> fetchGuilds() throws IOException
    {
        return Arrays.asList(toObject(handleGet("/users/@me/guilds"), Guild[].class));
    }

    public Member fetchMember(Guild guild, User user) throws IOException {
        return toObject(handleGet("/guilds/" + guild.getId() + "/members/" + user.getId()), Member.class);
    }

    /**
     * Make a specific user join a guild with the provided ID.
     * <br>This require the <code>guilds.join</code> scope.
     * <br>We use <code>PUT</code> method to join the guild.
     * @param guildId The guild ID to join.
     * @throws IOException If the request failed somehow.
     */
    public void joinGuild(String guildId) throws IOException {
        final User user = fetchUser();
        final String path = String.format("/guilds/%s/members/%s", guildId, user.getId());
        final org.jsoup.Connection request = Jsoup.connect(BASE_URI + path).ignoreContentType(true);
        setHeaders(request);
        request.method(org.jsoup.Connection.Method.PUT);
        request.execute();
    }

    public List<Connection> fetchConnections() throws IOException
    {
        return Arrays.asList(toObject(handleGet("/users/@me/connections"), Connection[].class));
    }
}
