package com.example.myauthapp.web.user.mapper;

import org.springframework.http.HttpStatus;
import com.example.myauthapp.web.user.error.UserError;
import com.example.myauthapp.user.error.MeUserError;
import com.example.myauthapp.user.error.UserDTOUserError;
import com.example.myauthapp.note.error.NoteDTONoteError;

public final class UserErrorMapper {

    private UserErrorMapper() {
    }

    public static HttpStatus toHttpStatus(UserError userError) {

        if (userError instanceof MeUserError error) {

            switch (error) {

                case ME_USER_USER_NOT_FOUND:
                    return HttpStatus.NOT_FOUND;

                default:
                    throw new IllegalArgumentException(
                        "Unknown MeUserError: " + error
                    );
            }
        }else  if (userError instanceof UserDTOUserError error) {

            switch (error) {

                case NO_USERS_ARE_FOUND:
                    return HttpStatus.NOT_FOUND;

                case NO_USER_IDS_ARE_FOUND:
                    return HttpStatus.NOT_FOUND;

                default:
                    throw new IllegalArgumentException(
                        "Unknown UserDTOUserError: " + error
                    );
            }
        }

        throw new IllegalArgumentException(
            "Unknown UserError: " + userError
        );
    }
    
}