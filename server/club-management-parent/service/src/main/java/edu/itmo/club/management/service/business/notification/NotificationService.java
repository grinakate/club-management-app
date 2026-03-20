package edu.itmo.club.management.service.business.notification;

import edu.itmo.club.management.domain.entity.Event;
import edu.itmo.club.management.domain.entity.MembershipApplication;
import edu.itmo.club.management.domain.entity.Notification;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface NotificationService {

	List<Notification> findByUserId(@NotNull Long userId);

	long countUnread(@NotNull Long userId);

	Notification markAsRead(@NotNull Long id, @NotNull Long userId);

	void markAllAsRead(@NotNull Long userId);

	void notifyClubMembersOnEventCreated(@NotNull Event event);

	void notifyRegistrantsOnEventCancelled(@NotNull Event event);

	void notifyApplicantOnApplicationReviewed(@NotNull MembershipApplication application);
}
