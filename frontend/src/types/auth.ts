export type Role = 'ROLE_ADMIN' | 'ROLE_WARDEN' | 'ROLE_STUDENT';
export type Gender = 'MALE' | 'FEMALE' | 'OTHER';

export interface ApiResponse<T> {
  status: string;
  statusCode: number;
  message: string;
  data: T;
}

export interface User {
  id: number;
  username: string;
  email: string;
  role: Role;
  firstName?: string;
  lastName?: string;
  phoneNumber?: string;
  studentId?: number;
  rollNumber?: string;
  department?: string;
  academicYear?: number;
  gender?: Gender;
  wardenId?: number;
  employeeId?: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  user: User;
}

export interface LoginCredentials {
  usernameOrEmail: string;
  password: string;
}

export interface RegisterCredentials {
  username: string;
  email: string;
  password: string;
  role: Role;
  firstName?: string;
  lastName?: string;
  phoneNumber?: string;
  rollNumber?: string;
  department?: string;
  academicYear?: number;
  gender?: Gender;
  guardianName?: string;
  guardianPhone?: string;
  address?: string;
  employeeId?: string;
  qualification?: string;
}
