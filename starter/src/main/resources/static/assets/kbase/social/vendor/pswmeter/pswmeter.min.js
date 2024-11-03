function passwordStrengthMeter(a){function b(){let a=c();d(a)}function c(){let a=0,b=/(?=.*[a-z])/,c=/(?=.*[A-Z])/,d=/(?=.*[0-9])/,e=new RegExp("(?=.{"+j+",})");return i.match(b)&&++a,i.match(c)&&++a,i.match(d)&&++a,i.match(e)&&++a,0==a&&0<i.length&&++a,a}function d(a){1===a?(g.className="password-strength-meter-score psms-25",k&&(k.textContent=l[1]||"Too simple"),f.dispatchEvent(new Event("onScore1",{bubbles:!0}))):2===a?(g.className="password-strength-meter-score psms-50",k&&(k.textContent=l[2]||"Simple"),f.dispatchEvent(new Event("onScore2",{bubbles:!0}))):3===a?(g.className="password-strength-meter-score psms-75",k&&(k.textContent=l[3]||"That's OK"),f.dispatchEvent(new Event("onScore3",{bubbles:!0}))):4===a?(g.className="password-strength-meter-score psms-100",k&&(k.textContent=l[4]||"Great password!"),f.dispatchEvent(new Event("onScore4",{bubbles:!0}))):(g.className="password-strength-meter-score",k&&(k.textContent=l[0]||"No data"),f.dispatchEvent(new Event("onScore0",{bubbles:!0})))}const e=document.createElement("style");document.body.prepend(e),e.innerHTML=`
    ${a.containerElement} {
      height: ${a.height||4}px;
      background-color: #eee;
      position: relative;
      overflow: hidden;
      border-radius: ${a.borderRadius.toString()||2}px;
    }
    ${a.containerElement} .password-strength-meter-score {
      height: inherit;
      width: 0%;
      transition: .3s ease-in-out;
      background: ${a.colorScore1||"#ff7700"};
    }
    ${a.containerElement} .password-strength-meter-score.psms-25 {width: 25%; background: ${a.colorScore1||"#ff7700"};}
    ${a.containerElement} .password-strength-meter-score.psms-50 {width: 50%; background: ${a.colorScore2||"#ffff00"};}
    ${a.containerElement} .password-strength-meter-score.psms-75 {width: 75%; background: ${a.colorScore3||"#aeff00"};}
    ${a.containerElement} .password-strength-meter-score.psms-100 {width: 100%; background: ${a.colorScore4||"#00ff00"};}`;const f=document.getElementById(a.containerElement.slice(1));f.classList.add("password-strength-meter");let g=document.createElement("div");g.classList.add("password-strength-meter-score"),f.appendChild(g);const h=document.getElementById(a.passwordInput.slice(1));let i="";h.addEventListener("keyup",function(){i=this.value,b()});let j=a.pswMinLength||8,k=a.showMessage?document.getElementById(a.messageContainer.slice(1)):null,l=void 0===a.messagesList?["No data","Too simple","Simple","That's OK","Great password!"]:a.messagesList;return k&&(k.textContent=l[0]||"No data"),{containerElement:f,getScore:c}}