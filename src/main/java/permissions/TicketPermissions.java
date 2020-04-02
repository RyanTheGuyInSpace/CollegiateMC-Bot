package permissions;

import net.dv8tion.jda.api.Permission;

import java.util.Arrays;
import java.util.Collection;

public class TicketPermissions {

	public static Collection<Permission> allowedPerms = Arrays.asList(Permission.MESSAGE_READ, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_WRITE, Permission.VIEW_CHANNEL);
	public static Collection<Permission> disallowedPerms = Arrays.asList(Permission.CREATE_INSTANT_INVITE, Permission.ADMINISTRATOR);

	// TODO Currently when a user creates a new ticket, they are given permission to see all channels in the issue/suggestions category. Prevent this.
	// TODO They should only be able to see their ticket and nobody else's

}
