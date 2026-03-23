package com.career.analytics.exception;

public class ProjectAchievementNotFoundException extends RuntimeException {
    public ProjectAchievementNotFoundException(String message) {

      super(message);
    }
    public ProjectAchievementNotFoundException(String message, Throwable cause) {
      super(message, cause);
    }
  public ProjectAchievementNotFoundException(Long projectId) {
    super("프로젝트 정보를 찾을 수 없습니다. ID: " + projectId);
  }
}
