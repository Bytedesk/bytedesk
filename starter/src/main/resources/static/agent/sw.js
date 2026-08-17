/*
 * 微语 Bytedesk Web App - Service Worker
 *
 * 仅在 Web 模式（非 Electron）下由 src/pwa.ts 注册。
 * 策略（刻意保持保守，避免影响实时通讯）：
 *   1. 导航请求 (mode === 'navigate')：网络优先，失败时回退缓存的 index.html，再回退 offline.html
 *   2. 同源静态资源 (/agent/assets/ 下的 js/css/img/font)：stale-while-revalidate（文件名带 hash，可安全缓存）
 *   3. 其余请求（API、跨域等）直接走网络，不做任何缓存
 */
const SW_VERSION = "bytedesk-agent-v2";
const STATIC_CACHE = `${SW_VERSION}-static`;
const NAV_CACHE = `${SW_VERSION}-nav`;
const OFFLINE_URL = "offline.html";

self.addEventListener("install", (event) => {
  event.waitUntil(
    (async () => {
      const cache = await caches.open(NAV_CACHE);
      // 预缓存离线兜底页；index.html 运行时缓存，避免预缓存与 hash 资源不一致
      await cache.add(new Request(OFFLINE_URL, { cache: "reload" }));
      await self.skipWaiting();
    })(),
  );
});

self.addEventListener("activate", (event) => {
  event.waitUntil(
    (async () => {
      // 清理旧版本缓存
      const keys = await caches.keys();
      await Promise.all(
        keys
          .filter((key) => key !== STATIC_CACHE && key !== NAV_CACHE)
          .map((key) => caches.delete(key)),
      );
      await self.clients.claim();
    })(),
  );
});

// 允许页面通过 postMessage({type: 'SKIP_WAITING'}) 立即激活新版本
self.addEventListener("message", (event) => {
  if (event.data && event.data.type === "SKIP_WAITING") {
    self.skipWaiting();
  }
});

function isStaticAssetRequest(url) {
  // 仅缓存同源、/agent/assets/ 下的构建产物（带内容 hash），以及图标等静态文件
  if (url.origin !== self.location.origin) return false;
  const p = url.pathname;
  return (
    p.includes("/assets/") ||
    p.startsWith("/agent/icons/") ||
    /\.(js|css|png|jpg|jpeg|gif|svg|webp|ico|woff2?|ttf|mp3|wav)$/i.test(p)
  );
}

self.addEventListener("fetch", (event) => {
  const { request } = event;
  if (request.method !== "GET") return;

  const url = new URL(request.url);
  // 跨域请求与 API/WebSocket 升级等一律直连网络
  if (url.origin !== self.location.origin) return;

  // 1. 页面导航：网络优先
  if (request.mode === "navigate") {
    event.respondWith(
      (async () => {
        try {
          const response = await fetch(request);
          // 仅缓存成功响应，避免把 404/500 等错误页缓存后污染导航回退
          if (response && response.ok) {
            const cache = await caches.open(NAV_CACHE);
            cache.put(request, response.clone());
          }
          return response;
        } catch (error) {
          const cached =
            (await caches.match(request)) ||
            (await caches.match(new URL(OFFLINE_URL, self.location.origin).href));
          return (
            cached ||
            new Response("Offline", { status: 503, statusText: "Offline" })
          );
        }
      })(),
    );
    return;
  }

  // 2. 静态资源：stale-while-revalidate
  if (isStaticAssetRequest(url)) {
    event.respondWith(
      (async () => {
        const cached = await caches.match(request);
        if (cached) {
          // 后台刷新缓存；waitUntil 保证 SW 在写入完成前不会被终止
          event.waitUntil(
            fetch(request)
              .then((response) => {
                if (response && response.ok) {
                  const clone = response.clone();
                  return caches.open(STATIC_CACHE).then((cache) =>
                    cache.put(request, clone),
                  );
                }
              })
              .catch(() => undefined),
          );
          return cached;
        }
        // 无缓存：取网络并写入缓存
        const response = await fetch(request);
        if (response && response.ok) {
          const clone = response.clone();
          event.waitUntil(
            caches
              .open(STATIC_CACHE)
              .then((cache) => cache.put(request, clone))
              .catch(() => undefined),
          );
        }
        return response;
      })(),
    );
  }
  // 3. 其它请求不调用 respondWith，交给浏览器默认网络行为
});
