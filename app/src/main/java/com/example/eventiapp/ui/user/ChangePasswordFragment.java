package com.example.eventiapp.ui.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eventiapp.R;
import com.example.eventiapp.databinding.FragmentChangePasswordBinding;
import com.example.eventiapp.model.Result;
import com.example.eventiapp.model.User;
import com.example.eventiapp.repository.user.IUserRepository;
import com.example.eventiapp.ui.welcome.ResetPasswordFragmentDirections;
import com.example.eventiapp.ui.welcome.UserViewModel;
import com.example.eventiapp.ui.welcome.UserViewModelFactory;
import com.example.eventiapp.util.ServiceLocator;
import com.google.android.material.snackbar.Snackbar;


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
                String oldPassword = fragmentChangePasswordBinding.editTextTextOldPassword.getText().toString().trim();
                String newPassword = fragmentChangePasswordBinding.editTextTextNewPassword.getText().toString().trim();
                String repeatedNewPassword = fragmentChangePasswordBinding.editTextTextNewPassword2.getText().toString().trim();

                if(newPassword.equals(repeatedNewPassword)){
                    if (isPasswordOk(newPassword)){
                        if(!userViewModel.isPasswordChangeError()){
                            userViewModel.getChangePasswordMutableLiveData(oldPassword,newPassword).observe(getViewLifecycleOwner(), result -> {
                                if (result.isSuccess()) {
                                    String message = ((Result.ChangePasswordSuccess) result).getMessage();
                                    userViewModel.setPasswordChangeError(false);
                                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                    Navigation.findNavController(view).navigate(ChangePasswordFragmentDirections.actionChangePasswordFragmentToAccountFragment());
                                } else {
                                    userViewModel.setPasswordChangeError(true);
                                    Toast.makeText(getContext(), ((Result.Error) result).getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else {
                            userViewModel.changePassword(oldPassword, newPassword);
                        }

                    }else {
                        Toast.makeText(getContext(), "Weak password", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getContext(), "Passwords are not equal", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean isPasswordOk(String password) {
        //da fare: controllo che la password sia buona
            return true;

    }
}