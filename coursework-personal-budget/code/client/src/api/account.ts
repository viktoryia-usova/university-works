import api from './axios'

export const accountApi = {
  changePassword: (id: number, oldPassword: string, newPassword: string) =>
    api.patch(`/account/${id}/password`, { oldPassword, newPassword }),
}
