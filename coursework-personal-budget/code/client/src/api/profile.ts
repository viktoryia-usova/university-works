import api from './axios'
import type {
  ProfileResponse, UpdateProfileRequest,
  PrivacySettingsResponse, NotificationSettingsResponse,
} from '../types'

export const profileApi = {
  getMe: () =>
    api.get<ProfileResponse>('/profile/me'),

  updateMe: (data: UpdateProfileRequest) =>
    api.patch<ProfileResponse>('/profile/me', data),

  getPrivacy: () =>
    api.get<PrivacySettingsResponse>('/profile/me/privacy'),

  updatePrivacy: (data: Partial<PrivacySettingsResponse>) =>
    api.put<PrivacySettingsResponse>('/profile/me/privacy', data),

  getNotifications: () =>
    api.get<NotificationSettingsResponse>('/profile/me/notifications'),

  updateNotifications: (data: Partial<NotificationSettingsResponse>) =>
    api.put<NotificationSettingsResponse>('/profile/me/notifications', data),
}
