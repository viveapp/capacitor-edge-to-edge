import { EdgeToEdge } from 'capacitor-android-e2e';

window.testEcho = () => {
    const inputValue = document.getElementById("echoInput").value;
    EdgeToEdge.echo({ value: inputValue })
}
