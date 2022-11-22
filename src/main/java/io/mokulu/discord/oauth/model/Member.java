package io.mokulu.discord.oauth.model;

import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

@Data
@Setter(AccessLevel.PRIVATE)
public class Member {

	private User user;
	private @Nullable String nick;
	private @Nullable String avatar;
	private String[] roles;
	@SerializedName("joined_at")
	private @NonNull String joinedAt;
	@SerializedName("premium_since")
	private @Nullable String premiumSince;
	private boolean deaf;
	private boolean mute;
	private boolean pending;

	public boolean isBoosting() {
		return premiumSince != null;
	}
}
