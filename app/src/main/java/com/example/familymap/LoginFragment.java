package com.example.familymap;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import logger.LoggerConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import request.LoginRequest;
import request.RegisterRequest;
import result.LoginRegisterResult;

public class LoginFragment extends Fragment {
    private Listener listener;
    private Button signInButton;
    private Button registerButton;
    View loginView;

    private final Logger logger = Logger.getLogger("LoginFragment");

    private static final String LOGIN_SUCCESS_KEY = "LoginSuccessKey";
    private static final String REGISTER_SUCCESS_KEY = "RegisterSuccessKey";

    public interface Listener {
        void notifyDone();
    }

    public void registerListener(Listener listener) {
        this.listener = listener;
    }

    private final TextWatcher EditTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            checkFieldsForEmptyValues();
        }
    };

    private void checkFieldsForEmptyValues() {
        EditText serverHostEditText, serverPortEditText, usernameEditText, passwordEditText, firstNameEditText, lastNameEditText, emailEditText;

        serverHostEditText = ((EditText) loginView.findViewById(R.id.edit_text_server_host)); //TODO: This code is repeated
        serverPortEditText = ((EditText) loginView.findViewById(R.id.edit_text_server_port));
        usernameEditText = ((EditText) loginView.findViewById(R.id.edit_text_username));
        passwordEditText = ((EditText) loginView.findViewById(R.id.edit_text_password));
        firstNameEditText = ((EditText) loginView.findViewById(R.id.edit_text_first_name));
        lastNameEditText = ((EditText) loginView.findViewById(R.id.edit_text_last_name));
        emailEditText = ((EditText) loginView.findViewById(R.id.edit_text_email));

        String serverHost = serverHostEditText.getText().toString();
        String serverPort = serverPortEditText.getText().toString();
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String email = emailEditText.getText().toString();

        signInButton = (Button) loginView.findViewById(R.id.sign_in_button);
        registerButton = (Button) loginView.findViewById(R.id.register_button);

        if (serverHost.equals("") || serverPort.equals("") || username.equals("") || password.equals("")) {
            signInButton.setEnabled(false);
            registerButton.setEnabled(false);
        } else if (firstName.equals("") || lastName.equals("") || email.equals("")) {
            signInButton.setEnabled(true);
            registerButton.setEnabled(false);
        } else {
            signInButton.setEnabled(true);
            registerButton.setEnabled(true);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoggerConfig.configureLogger(logger, Level.FINEST);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saveInstanceState) {
        loginView = inflater.inflate(R.layout.fragment_login, container, false);

        signInButton = loginView.findViewById(R.id.sign_in_button);
        registerButton = loginView.findViewById(R.id.register_button);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Handler loginHandler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message message) {
                        Bundle bundle = message.getData();
                        boolean success = bundle.getBoolean(LOGIN_SUCCESS_KEY, false);

                        logger.fine("In loginHandler.handleMessage. Success: " + success);

                        String toast;
                        if (!success) {
                            toast = "Login Failed";
                        } else {
                            toast = DataCache.getUserFullName() + " is logged in";
                        }

                        logger.finest(getActivity().toString());
                        logger.finer(toast);
                        Toast.makeText(getActivity(), toast, Toast.LENGTH_SHORT).show();

                        if (success) {
                            listener.notifyDone();
                        }
                    }
                };

                LoginTask loginTask = new LoginTask(loginHandler, loginView);
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(loginTask);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Handler registerHandler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message message) {
                        Bundle bundle = message.getData();
                        boolean success = bundle.getBoolean(REGISTER_SUCCESS_KEY);

                        logger.fine("In registerHandler.handleMessage. Success: " + success);

                        String toast;
                        if (!success) {
                            toast = "Registration Failed";
                        } else {
                            toast = DataCache.getUserFullName() + " is registered";
                        }
                        logger.finer(toast);
                        Toast.makeText(getActivity(), toast, Toast.LENGTH_LONG).show();

                        if (success) {
                            listener.notifyDone();
                        }
                    }
                };

                RegisterTask registerTask = new RegisterTask(registerHandler, loginView);
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(registerTask);
            }
        });

        EditText serverHostEditText, serverPortEditText, usernameEditText, passwordEditText, firstNameEditText, lastNameEditText, emailEditText;

        serverHostEditText = ((EditText) loginView.findViewById(R.id.edit_text_server_host)); //TODO: This code is repeated
        serverPortEditText = ((EditText) loginView.findViewById(R.id.edit_text_server_port));
        usernameEditText = ((EditText) loginView.findViewById(R.id.edit_text_username));
        passwordEditText = ((EditText) loginView.findViewById(R.id.edit_text_password));
        firstNameEditText = ((EditText) loginView.findViewById(R.id.edit_text_first_name));
        lastNameEditText = ((EditText) loginView.findViewById(R.id.edit_text_last_name));
        emailEditText = ((EditText) loginView.findViewById(R.id.edit_text_email));

        serverHostEditText.addTextChangedListener(EditTextWatcher);
        serverPortEditText.addTextChangedListener(EditTextWatcher);
        usernameEditText.addTextChangedListener(EditTextWatcher);
        passwordEditText.addTextChangedListener(EditTextWatcher);
        firstNameEditText.addTextChangedListener(EditTextWatcher);
        lastNameEditText.addTextChangedListener(EditTextWatcher);
        emailEditText.addTextChangedListener(EditTextWatcher);

        checkFieldsForEmptyValues();

        return loginView;
    }

    private static class LoginTask implements Runnable {
        private final Handler messageHandler;
        private final View loginView;
        private final Logger logger = Logger.getLogger("LoginTask");

        LoginTask(Handler messageHandler, View loginView) {
            this.messageHandler = messageHandler;
            this.loginView = loginView;
        }

        @Override
        public void run() {
            LoggerConfig.configureLogger(logger, Level.FINEST);
            EditText serverHostEditText, serverPortEditText, usernameEditText, passwordEditText;

            serverHostEditText = ((EditText) loginView.findViewById(R.id.edit_text_server_host));
            serverPortEditText = ((EditText) loginView.findViewById(R.id.edit_text_server_port));
            usernameEditText = ((EditText) loginView.findViewById(R.id.edit_text_username));
            passwordEditText = ((EditText) loginView.findViewById(R.id.edit_text_password));

            String serverHost = serverHostEditText.getText().toString();
            String serverPort = serverPortEditText.getText().toString();
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            LoginRequest request = new LoginRequest(username, password);

            ServerProxy serverProxy = new ServerProxy(serverHost, serverPort);

            logger.fine("About to call login");
            LoginRegisterResult result = serverProxy.login(request);
            logger.fine("Called login");

            if (result != null) {
                sendMessage(result, messageHandler);
            }
        }
    }

    private static class RegisterTask implements Runnable {
        private final Handler messageHandler;
        private final View loginView;
        private final Logger logger = Logger.getLogger("RegisterTask");

        public RegisterTask(Handler messageHandler, View loginView) {
            this.messageHandler = messageHandler;
            this.loginView = loginView;
        }

        @Override
        public void run() {
            LoggerConfig.configureLogger(logger, Level.FINEST);
            logger.fine("In RegisterTask.run");

            String serverHost = ((EditText) loginView.findViewById(R.id.edit_text_server_host)).getText().toString();
            String serverPort = ((EditText) loginView.findViewById(R.id.edit_text_server_port)).getText().toString();

            ServerProxy serverProxy = new ServerProxy(serverHost, serverPort);

            RegisterRequest request = createRegisterRequest(loginView);

            logger.fine("Calling serverProxy.register");
            LoginRegisterResult result = serverProxy.register(request);

            sendMessage(result, messageHandler);
        }

        private RegisterRequest createRegisterRequest(View loginView) {
            String username = ((EditText) loginView.findViewById(R.id.edit_text_username)).getText().toString();
            String password = ((EditText) loginView.findViewById(R.id.edit_text_password)).getText().toString();
            String firstName = ((EditText) loginView.findViewById(R.id.edit_text_first_name)).getText().toString();
            String lastName = ((EditText) loginView.findViewById(R.id.edit_text_last_name)).getText().toString();
            String email = ((EditText) loginView.findViewById(R.id.edit_text_email)).getText().toString();

            RadioGroup genderRadioGroup = ((RadioGroup) loginView.findViewById(R.id.gender_radio_group));
            int selectedRadioID = genderRadioGroup.getCheckedRadioButtonId();
            String selectedGender = ((RadioButton) loginView.findViewById(selectedRadioID)).getText().toString();
            String gender;
            if (selectedGender.equals("Male")) {
                gender = "m";
            } else {
                gender = "f";
            }

            return new RegisterRequest(username, password, email, firstName, lastName, gender);
        }
    }

    private static void sendMessage(LoginRegisterResult result, Handler messageHandler) {
        Message message = Message.obtain();

        Bundle messageBundle = new Bundle();
        messageBundle.putBoolean(LOGIN_SUCCESS_KEY, result.isSuccess());
        message.setData(messageBundle);

        messageHandler.sendMessage(message);
    }
}
