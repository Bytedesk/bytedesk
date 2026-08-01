import e from "../utils/logger/index.js";
import t from "../index/index.js";
import { Component as n, Input as r } from "@angular/core";
//#region src/adapters/angular.ts
var i = null, a = 0, o = @n({
	selector: "bytedesk-angular",
	standalone: !1,
	template: "",
	styles: ["\n    :host {\n      display: none;\n    }\n  "]
}) class {
	@r() config;
	ngOnInit() {
		if (a++, i) {
			window.bytedesk = i;
			return;
		}
		i = new t(this.config), i.init(), window.bytedesk = i;
	}
	ngOnDestroy() {
		a--, e.debug("BytedeskAngular: 组件卸载，当前活跃组件数:", a), a <= 0 && (e.debug("BytedeskAngular: 没有活跃组件，清理全局实例"), setTimeout(() => {
			i && a <= 0 && (i.destroy(), i = null, delete window.bytedesk, a = 0);
		}, 100));
	}
};
//#endregion
export { o as BytedeskAngular };
