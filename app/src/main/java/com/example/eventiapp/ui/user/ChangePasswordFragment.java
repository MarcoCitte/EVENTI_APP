package com.example.eventiapp.ui.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eventiapp.R;
import com.example.eventiapp.databinding.FragmentAccountBinding;
import com.example.eventiapp.databinding.FragmentChangePasswordBinding;
import com.example.eventiapp.repository.user.IUserRepository;
import com.example.eventiapp.ui.welcome.UserViewModel;
import com.example.eventiapp.ui.welcome.UserViewModelFactory;
import com.example.eventiapp.util.ServiceLocator;


public class ChangePasswordFragment extends Fragment {

    private FragmentChangePasswordBinding fragmentChangePasswordBinding;
    private UserViewModel userViewModel;



    public ChangePasswordFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentChangePasswordBinding = FragmentChangePasswordBinding.inflate(inflater, container, false);
        return fragmentChangePasswordBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentChangePasswordBinding.buttonConfirmNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = fragmentChangePasswordBinding.editTextTextPassword.getText().toString().trim();
                String repeatedPassword = fragmentChangePasswordBinding.editTextTextPassword2.getText().toString().trim();

                if(password.equals(repeatedPassword)){
                    if (isPasswordOk(password)){
                        userViewModel.changePassword(password);
                    }else {
                        Toast.makeText(getContext(), "Passwords are not equal", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getContext(), "Passwords are not equal", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isPasswordOk(String password) {
        // Check if the password length is correct
        if (password.isEmpty()) {
            Toast.makeText(getContext(), getString(R.string.error_password), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            //textInputLayoutPassword.setError(null);
            return true;
        }
    }
}