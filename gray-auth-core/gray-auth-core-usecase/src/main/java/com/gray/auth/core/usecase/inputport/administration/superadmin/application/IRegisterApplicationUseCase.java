package com.gray.auth.core.usecase.inputport.administration.superadmin.application;

import com.gary.auth.core.domain.AccountRole;
import com.gary.auth.core.domain.Application;
import com.gary.auth.core.domain.exception.IllegalInputException;
import com.gray.auth.core.usecase.outputport.IAuthPersistenceGateway;
import com.gray.auth.core.usecase.outputport.IAuthPersistenceGateway.DBApplication;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.EnumSet;
import java.util.function.Function;

public interface IRegisterApplicationUseCase extends Function<IRegisterApplicationUseCase.RegisterApplicationRequest, IRegisterApplicationUseCase.RegisterApplicationResponse> {

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    final class RegisterApplicationRequest {
        private String applicationName;
        private EnumSet<AccountRole> actorRoles;
    }

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    final class RegisterApplicationResponse {
        private String message;
    }

    static IRegisterApplicationUseCase newInstance(IAuthPersistenceGateway repo) {
        return new DefaultRegisterApplicationUseCase(repo);
    }

    @AllArgsConstructor
    class DefaultRegisterApplicationUseCase implements IRegisterApplicationUseCase {

        private final IAuthPersistenceGateway applicationRepo;

        @Override
        public RegisterApplicationResponse apply(RegisterApplicationRequest req) {

            Application application = Application.create(req.applicationName, req.actorRoles);

            if (this.applicationRepo.isAppExistsByName(application.getApplicationName())) {
                throw new IllegalInputException("Application is already exist");
            }

            DBApplication dbApplication = DBApplication.from(application);
            this.applicationRepo.persist(dbApplication);

            return new RegisterApplicationResponse("Application Created Successfully");
        }
    }
}
