import React, { useState } from 'react'
import './Login.css'

const Login: React.FC = () => {

    const [username, setUsername] = useState('')
    const [password, setPassword] = useState('')
    const [usernameError, setUsernameError] = useState('')
    const [passwordError, setPasswordError] = useState('')

    const createAccount = async () => {
        let response = await fetch('/CreateAccount', {
            method: 'POST',
            headers: {
                'Content-type': 'application/json'
            },
            body: JSON.stringify({
                username: username,
                password: password,
            })
        })
        if (!response.ok) {
            setUsernameError("SOMETHING WENT WRONG")
            setPasswordError("SOMETHING WENT WRONG")
            return;
        } 
        const { userId } = await response.json();
        //if (globalStore?.setSession) {
        //    globalStore.setSession((prevSession) => ({
        //        ...prevSession,  // Spread previous session values
        //        userID: userId,  // Update the userID
        //    }));
        //}
    }

    const logIn = async () => {
        let response = await fetch('/LogIn', {
            method: 'POST',
            headers: {
                'Content-type': 'application/json'
            },
            body: JSON.stringify({
                us: username,
                pa: password,
            })
        })
        if (!response.ok) {
            setUsernameError("SOMETHING WENT WRONG")
            setPasswordError("SOMETHING WENT WRONG")
            return;
        } else {
            //setLoggedIn(true);
            //response.json().then(val => setUserId(val.userId));
        }
    }

    return (
        <div className="mainContainer">
            <div className="formWrapper">
                {/* Username Input */}
                <div className="inputContainer">
                    <input
                        value={username}
                        placeholder="<Enter your username>"
                        onChange={(ev) => setUsername(ev.target.value)}
                        className="inputBox"
                    />
                    {usernameError && <label className="errorLabel">{usernameError}</label>}
                </div>

                {/* Password Input */}
                <div className="inputContainer">
                    <input
                        type="password"
                        value={password}
                        placeholder="<Enter your password>"
                        onChange={(ev) => setPassword(ev.target.value)}
                        className="inputBox"
                    />
                    {passwordError && <label className="errorLabel">{passwordError}</label>}
                </div>

                {/* Buttons */}
                <div className="buttonsContainer">
                    <input
                        className="inputButton"
                        type="button"
                        onClick={logIn}
                        value="<Log in>"
                    />
                    <input
                        className="inputButton secondary"
                        type="button"
                        onClick={createAccount}
                        value="<Create Account>"
                    />
                </div>
            </div>
        </div>

    );
}

export default Login
