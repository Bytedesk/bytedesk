window.onload = () => {
    if (isElectron) {
        let text= document.getElementById("snap-text");
        let switchtext = document.getElementById("switch");

        fetch('/ip').then((res) => {
            return res.json();
        }).then((data) => {
            let ipaddr = Object.values(data).flat();         
            let x = 0;
            text.innerHTML = `Use a browser to go to <div id=\"ip-text\">http://${ipaddr[0]}:3001</div> on your other devices`;
            switchtext.addEventListener("click",((e) => {
                e.preventDefault();
                if (x === ipaddr.length -1) x = 0;
                else x+=1;
                let iptext = document.getElementById("ip-text");
                iptext.innerText = `http://${ipaddr[x]}:3001`;
            }));
        });
    }
    function isElectron() {
    
    
        // Main process
        if (typeof process !== 'undefined' && typeof process.versions === 'object' && !!process.versions.electron) {
            return true;
        }
    
        // Detect the user agent when the `nodeIntegration` option is set to false
        if (typeof navigator === 'object' && typeof navigator.userAgent === 'string' && navigator.userAgent.indexOf('Electron') >= 0) {
            return true;
        }
    
        return false;
    }
}