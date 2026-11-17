package com.career.analytics.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QResume is a Querydsl query type for Resume
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QResume extends EntityPathBase<Resume> {

    private static final long serialVersionUID = -121686035L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QResume resume = new QResume("resume");

    public final ListPath<Award, QAward> awards = this.<Award, QAward>createList("awards", Award.class, QAward.class, PathInits.DIRECT2);

    public final ListPath<Certification, QCertification> certifications = this.<Certification, QCertification>createList("certifications", Certification.class, QCertification.class, PathInits.DIRECT2);

    public final QCoverLetter coverLetter;

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final ListPath<Education, QEducation> educations = this.<Education, QEducation>createList("educations", Education.class, QEducation.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isDefault = createBoolean("isDefault");

    public final ListPath<Language, QLanguage> languages = this.<Language, QLanguage>createList("languages", Language.class, QLanguage.class, PathInits.DIRECT2);

    public final ListPath<Portfolio, QPortfolio> portfolios = this.<Portfolio, QPortfolio>createList("portfolios", Portfolio.class, QPortfolio.class, PathInits.DIRECT2);

    public final StringPath title = createString("title");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final QUsers user;

    public final ListPath<WorkExperience, QWorkExperience> workExperiences = this.<WorkExperience, QWorkExperience>createList("workExperiences", WorkExperience.class, QWorkExperience.class, PathInits.DIRECT2);

    public QResume(String variable) {
        this(Resume.class, forVariable(variable), INITS);
    }

    public QResume(Path<? extends Resume> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QResume(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QResume(PathMetadata metadata, PathInits inits) {
        this(Resume.class, metadata, inits);
    }

    public QResume(Class<? extends Resume> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.coverLetter = inits.isInitialized("coverLetter") ? new QCoverLetter(forProperty("coverLetter"), inits.get("coverLetter")) : null;
        this.user = inits.isInitialized("user") ? new QUsers(forProperty("user"), inits.get("user")) : null;
    }

}

