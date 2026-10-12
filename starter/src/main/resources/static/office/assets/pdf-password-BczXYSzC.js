const api = window.aiOfficePdfPassword;
const el = (id) => document.getElementById(id);
const title = el("title");
const file = el("file");
const prompt = el("prompt");
const label = el("label");
const pwdWrap = el("pwd-wrap");
const password = el("password");
const eye = el("eye");
const error = el("error");
const errorText = el("error-text");
const ok = el("ok");
const cancel = el("cancel");
let busy = false;
let showLabel = "";
let hideLabel = "";
function render(state) {
  busy = state.busy;
  const s = state.strings;
  showLabel = s.show;
  hideLabel = s.hide;
  document.documentElement.lang = state.lang;
  document.title = s.title;
  title.textContent = s.title;
  file.textContent = state.fileName;
  file.title = state.fileName;
  prompt.textContent = state.busy ? s.verifying : s.prompt;
  label.textContent = s.label;
  password.placeholder = s.placeholder;
  ok.textContent = s.ok;
  cancel.textContent = s.cancel;
  const failed = state.retry && !state.busy;
  if (failed) password.value = "";
  password.classList.toggle("invalid", failed);
  pwdWrap.classList.toggle("has-error", failed);
  error.classList.toggle("visible", failed);
  errorText.textContent = failed ? s.retryPrompt : "";
  password.disabled = state.busy;
  eye.disabled = state.busy;
  eye.setAttribute("aria-label", eye.classList.contains("showing") ? hideLabel : showLabel);
  ok.disabled = state.busy || !password.value;
  cancel.disabled = state.busy;
  if (!state.busy) password.focus();
}
function submit() {
  if (busy || !password.value) return;
  api.submit(password.value);
}
ok.addEventListener("click", submit);
cancel.addEventListener("click", () => {
  if (!busy) api.cancel();
});
password.addEventListener("input", () => {
  ok.disabled = busy || !password.value;
  password.classList.remove("invalid");
  pwdWrap.classList.remove("has-error");
  error.classList.remove("visible");
});
password.addEventListener("keydown", (e) => {
  if (e.key === "Enter") submit();
});
eye.addEventListener("mousedown", (e) => e.preventDefault());
eye.addEventListener("click", () => {
  const show = password.type === "password";
  const caret = [
    password.selectionStart ?? password.value.length,
    password.selectionEnd ?? password.value.length
  ];
  password.type = show ? "text" : "password";
  eye.classList.toggle("showing", show);
  eye.setAttribute("aria-label", show ? hideLabel : showLabel);
  const restore = () => {
    if (document.activeElement === password) password.setSelectionRange(caret[0], caret[1]);
  };
  restore();
  requestAnimationFrame(restore);
});
document.addEventListener("keydown", (e) => {
  if (e.key === "Escape" && !busy) api.cancel();
});
api.onState(render);
void api.getState().then((state) => {
  if (state) render(state);
});
