package com.career.analytics.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPortfolio is a Querydsl query type for Portfolio
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPortfolio extends EntityPathBase<Portfolio> {

    private static final long serialVersionUID = -1550287480L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPortfolio portfolio = new QPortfolio("portfolio");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath portfolioName = createString("portfolioName");

    public final QResume resume;

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final StringPath url = createString("url");

    public QPortfolio(String variable) {
        this(Portfolio.class, forVariable(variable), INITS);
    }

    public QPortfolio(Path<? extends Portfolio> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPortfolio(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPortfolio(PathMetadata metadata, PathInits inits) {
        this(Portfolio.class, metadata, inits);
    }

    public QPortfolio(Class<? extends Portfolio> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.resume = inits.isInitialized("resume") ? new QResume(forProperty("resume"), inits.get("resume")) : null;
    }

}

