package com.example.eventiapp.ui.welcome;

import static com.example.eventiapp.util.Constants.INVALID_CREDENTIALS_ERROR;
import static com.example.eventiapp.util.Constants.INVALID_USER_ERROR;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.eventiapp.R;
import com.example.eventiapp.model.Result;
import com.example.eventiapp.model.User;
import com.example.eventiapp.repository.user.IUserRepository;
import com.example.eventiapp.util.ServiceLocator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.apache.commons.validator.routines.EmailValidator;


public class ResetPasswordFragment extends Fragment {

    private UserViewModel userViewModel;
    private TextInputLayout textInputLayoutEmail;
    private Button buttonSendEmail;
    private TextView resultTV;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reset_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        textInputLayoutEmail = view.findViewById(R.id.email_et);
        buttonSendEmail = view.findViewById(R.id.email_b);
        resultTV = view.findViewById(R.id.result_tv);

        buttonSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = textInputLayoutEmail.getEditText().getText().toString();

                if(isEmailOk(email)){
                        if (!userViewModel.isPasswordResetEmailError()) {
                            userViewModel.getResetPasswordMutableLiveData(email).observe(
                                            getViewLifecycleOwner(), result -> {
                                                if (result.isSuccess()) {
                                                    String message = ((Result.ResetPasswordSuccess) result).getMessage();
                                                    userViewModel.setPasswordResetEmailError(false);
                                                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                                            message,
                                                            Snackbar.LENGTH_SHORT).show();
                                                    Navigation.findNavController(view).navigate(ResetPasswordFragmentDirections.actionResetPasswordFragmentToLoginFragment());
                                                } else {
                                                    userViewModel.setPasswordResetEmailError(true);
                                                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                                            getErrorMessage(((Result.Error) result).getMessage()),
                                                            Snackbar.LENGTH_SHORT).show();
                                                }
                                            });
                        } else {
                            userViewModel.resetPassword(email);
                        }
                    } else {
                        Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                R.string.check_login_data_message, Snackbar.LENGTH_SHORT).show();
                    }
                }
        });


    }

    private boolean isEmailOk(String email) {
        // Check if the email is valid through the use of this library:
        // https://commons.apache.org/proper/commons-validator/
        if (!EmailValidator.getInstance().isValid((email))) {
            textInputLayoutEmail.setError(getString(R.string.error_email));
            return false;
        } else {
            textInputLayoutEmail.setError(null);
            return true;
        }
    }

    /**
     * Returns the text to be shown to the user based on the type of error.
     * @param errorType The type of error.
     * @return The message to be shown to the user.
     */
    private String getErrorMessage(String errorType) {
        switch (errorType) {
            case INVALID_USER_ERROR:
                return requireActivity().getString(R.string.error_login_user_message);
            default:
                return requireActivity().getString(R.string.unexpected_error);
        }
    }


}