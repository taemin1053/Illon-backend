package com.career.analytics.exception;

import com.career.analytics.entity.Portfolio;

public class PortfolioNotFoundException extends RuntimeException {
  private Long id;
  public PortfolioNotFoundException(String message) {
    super(message);
  }

  public PortfolioNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
  public PortfolioNotFoundException(Long id) {
    super("포트폴리오를 찾을수 없습니다 ID : " + id);
    this.id = id;
  }
  public Long getId() {
    return id;
  }


}
