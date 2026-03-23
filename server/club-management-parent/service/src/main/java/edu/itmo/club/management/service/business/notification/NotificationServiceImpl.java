package edu.itmo.club.management.service.business.notification;

import edu.itmo.club.management.domain.entity.Club;
import edu.itmo.club.management.domain.entity.Event;
import edu.itmo.club.management.domain.entity.MembershipApplication;
import edu.itmo.club.management.domain.entity.Notification;
import edu.itmo.club.management.domain.entity.User;
import edu.itmo.club.management.domain.enums.ApplicationStatus;
import edu.itmo.club.management.domain.enums.DeliveryStatus;
import edu.itmo.club.management.domain.enums.MembershipStatus;
import edu.itmo.club.management.domain.enums.NotificationChannel;
import edu.itmo.club.management.domain.enums.RegistrationStatus;
import edu.itmo.club.management.domain.repository.ClubMembershipRepository;
import edu.itmo.club.management.domain.repository.EventRegistrationRepository;
import edu.itmo.club.management.domain.repository.NotificationRepository;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {

	private final NotificationRepository notificationRepository;
	private final ClubMembershipRepository clubMembershipRepository;
	private final EventRegistrationRepository eventRegistrationRepository;

	@Transactional(readOnly = true)
	@Override
	public List<Notification> findByUserId(@NotNull Long userId) {
		return notificationRepository.findByUserIdOrderByScheduledAtDesc(userId);
	}

	@Transactional(readOnly = true)
	@Override
	public long countUnread(@NotNull Long userId) {
		return notificationRepository.countByUserIdAndIsReadFalse(userId);
	}

	@Transactional
	@Override
	public Notification markAsRead(@NotNull Long id, @NotNull Long userId) {
		Notification notification = notificationRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Уведомление не найдено"));
		if (!notification.getUser().getId().equals(userId)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нет доступа к этому уведомлению");
		}
		notification.setIsRead(true);
		return notificationRepository.save(notification);
	}

	@Transactional
	@Override
	public void markAllAsRead(@NotNull Long userId) {
		notificationRepository.markAllAsReadByUserId(userId);
	}

	@Transactional
	@Override
	public void notifyClubMembersOnEventCreated(@NotNull Event event) {
		clubMembershipRepository.findByClubIdAndStatus(event.getClub().getId(), MembershipStatus.ACTIVE)
				.forEach(membership -> createNotification(
						membership.getUser(),
						event,
						event.getClub(),
						"Новое мероприятие в клубе «" + event.getClub().getName() + "»",
						"В клубе «" + event.getClub().getName() + "» появилось новое мероприятие: " + event.getTitle()
				));
	}

	@Transactional
	@Override
	public void notifyRegistrantsOnEventCancelled(@NotNull Event event) {
		eventRegistrationRepository.findByEventId(event.getId())
				.stream()
				.filter(r -> r.getStatus() == RegistrationStatus.REGISTERED)
				.forEach(reg -> createNotification(
						reg.getUser(),
						event,
						event.getClub(),
						"Мероприятие отменено",
						"Мероприятие «" + event.getTitle() + "» было отменено"
				));
	}

	@Transactional
	@Override
	public void notifyApplicantOnApplicationReviewed(@NotNull MembershipApplication application) {
		boolean approved = application.getStatus() == ApplicationStatus.APPROVED;
		String subject = approved ? "Заявка в клуб одобрена" : "Заявка в клуб отклонена";
		String message = approved
				? "Ваша заявка в клуб «" + application.getClub().getName() + "» одобрена"
				: "Ваша заявка в клуб «" + application.getClub().getName() + "» отклонена";
		createNotification(application.getUser(), null, application.getClub(), subject, message);
	}

	private void createNotification(User user, Event event, Club club, String subject, String message) {
		Notification notification = new Notification();
		notification.setUser(user);
		notification.setEvent(event);
		notification.setClub(club);
		notification.setSubject(subject);
		notification.setMessageText(message);
		notification.setScheduledAt(LocalDateTime.now());
		notification.setChannel(NotificationChannel.IN_APP);
		notification.setIsRead(false);
		notification.setDeliveryStatus(DeliveryStatus.DELIVERED);
		notificationRepository.save(notification);
	}
}
