/**
* Social - Network, Community and Event Theme
*
* @author Webestica (https://www.webestica.com/)
* @version 1.1.0
**/


/* ===================
Table Of Content
======================
01 PRELOADER
02 NAVBAR DROPDOWN HOVER
03 TINY SLIDER
04 TOOLTIP
05 POPOVER
06 VIDEO PLAYER
07 GLIGHTBOX
08 SIDEBAR TOGGLE START
09 SIDEBAR TOGGLE END
10 CHOICES
11 AUTO RESIZE TEXTAREA
12 DROP ZONE
13 FLAT PICKER
14 AVATAR IMAGE
15 CUSTOM SCROLLBAR
16 TOASTS
17 PSWMETER
18 FAKE PASSWORD
====================== */

"use strict";
!function () {

		window.Element.prototype.removeClass = function () {
				let className = arguments.length > 0 && void 0 !== arguments[0] ? arguments[0] : "",
						selectors = this;
				if (!(selectors instanceof HTMLElement) && selectors !== null) {
						selectors = document.querySelector(selectors);
				}
				if (this.isVariableDefined(selectors) && className) {
						selectors.classList.remove(className);
				}
				return this;
		}, window.Element.prototype.addClass = function () {
				let className = arguments.length > 0 && void 0 !== arguments[0] ? arguments[0] : "",
						selectors = this;
				if (!(selectors instanceof HTMLElement) && selectors !== null) {
						selectors = document.querySelector(selectors);
				}
				if (this.isVariableDefined(selectors) && className) {
						selectors.classList.add(className);
				}
				return this;
		}, window.Element.prototype.toggleClass = function () {
				let className = arguments.length > 0 && void 0 !== arguments[0] ? arguments[0] : "",
						selectors = this;
				if (!(selectors instanceof HTMLElement) && selectors !== null) {
						selectors = document.querySelector(selectors);
				}
				if (this.isVariableDefined(selectors) && className) {
						selectors.classList.toggle(className);
				}
				return this;
		}, window.Element.prototype.isVariableDefined = function () {
				return !!this && typeof (this) != 'undefined' && this != null;
		}
}();


var e = {
		init: function () {
				e.preLoader(),
				e.navbarDropdownHover(),
				e.tinySlider(),
				e.toolTipFunc(),
				e.popOverFunc(),
				e.videoPlyr(),
				e.lightBox(),
				e.sidebarToggleStart(),
				e.sidebarToggleEnd(),
				e.choicesSelect(),
				e.autoResize(),
				e.DropZone(),
				e.flatPicker(),
				e.avatarImg(),
				e.customScrollbar(),
				e.toasts(),
				e.pswMeter(),
				e.fakePwd();
		},
		isVariableDefined: function (el) {
				return typeof !!el && (el) != 'undefined' && el != null;
		},
		getParents: function (el, selector, filter) {
				const result = [];
				const matchesSelector = el.matches || el.webkitMatchesSelector || el.mozMatchesSelector || el.msMatchesSelector;

				// match start from parent
				el = el.parentElement;
				while (el && !matchesSelector.call(el, selector)) {
						if (!filter) {
								if (selector) {
										if (matchesSelector.call(el, selector)) {
												return result.push(el);
										}
								} else {
										result.push(el);
								}
						} else {
								if (matchesSelector.call(el, filter)) {
										result.push(el);
								}
						}
						el = el.parentElement;
						if (e.isVariableDefined(el)) {
								if (matchesSelector.call(el, selector)) {
										return el;
								}
						}

				}
				return result;
		},
		getNextSiblings: function (el, selector, filter) {
				let sibs = [];
				let nextElem = el.parentNode.firstChild;
				const matchesSelector = el.matches || el.webkitMatchesSelector || el.mozMatchesSelector || el.msMatchesSelector;
				do {
						if (nextElem.nodeType === 3) continue; // ignore text nodes
						if (nextElem === el) continue; // ignore elem of target
						if (nextElem === el.nextElementSibling) {
								if ((!filter || filter(el))) {
										if (selector) {
												if (matchesSelector.call(nextElem, selector)) {
														return nextElem;
												}
										} else {
												sibs.push(nextElem);
										}
										el = nextElem;

								}
						}
				} while (nextElem = nextElem.nextSibling)
				return sibs;
		},
		on: function (selectors, type, listener) {
				document.addEventListener("DOMContentLoaded", () => {
						if (!(selectors instanceof HTMLElement) && selectors !== null) {
								selectors = document.querySelector(selectors);
						}
						selectors.addEventListener(type, listener);
				});
		},
		onAll: function (selectors, type, listener) {
				document.addEventListener("DOMContentLoaded", () => {
						document.querySelectorAll(selectors).forEach((element) => {
								if (type.indexOf(',') > -1) {
										let types = type.split(',');
										types.forEach((type) => {
												element.addEventListener(type, listener);
										});
								} else {
										element.addEventListener(type, listener);
								}


						});
				});
		},
		removeClass: function (selectors, className) {
				if (!(selectors instanceof HTMLElement) && selectors !== null) {
						selectors = document.querySelector(selectors);
				}
				if (e.isVariableDefined(selectors)) {
						selectors.removeClass(className);
				}
		},
		removeAllClass: function (selectors, className) {
				if (e.isVariableDefined(selectors) && (selectors instanceof HTMLElement)) {
						document.querySelectorAll(selectors).forEach((element) => {
								element.removeClass(className);
						});
				}

		},
		toggleClass: function (selectors, className) {
				if (!(selectors instanceof HTMLElement) && selectors !== null) {
						selectors = document.querySelector(selectors);
				}
				if (e.isVariableDefined(selectors)) {
						selectors.toggleClass(className);
				}
		},
		toggleAllClass: function (selectors, className) {
				if (e.isVariableDefined(selectors)  && (selectors instanceof HTMLElement)) {
						document.querySelectorAll(selectors).forEach((element) => {
								element.toggleClass(className);
						});
				}
		},
		addClass: function (selectors, className) {
				if (!(selectors instanceof HTMLElement) && selectors !== null) {
						selectors = document.querySelector(selectors);
				}
				if (e.isVariableDefined(selectors)) {
						selectors.addClass(className);
				}
		},
		select: function (selectors) {
				return document.querySelector(selectors);
		},
		selectAll: function (selectors) {
				return document.querySelectorAll(selectors);
		},

		// START: 01 Preloader
		preLoader: function () {
				window.onload = function () {
						var preloader = e.select('.preloader');
						if (e.isVariableDefined(preloader)) {
								preloader.className += ' animate__animated animate__fadeOut';
								setTimeout(function(){
										preloader.style.display = 'none';
								}, 200);
						}
				};
		},
		// END: Preloader

		// START: 02 Navbar dropdown hover
		navbarDropdownHover: function () {
				e.onAll('.dropdown-menu a.dropdown-item.dropdown-toggle', 'click', function (event) {
						var element = this;
						event.preventDefault();
						event.stopImmediatePropagation();
						if (e.isVariableDefined(element.nextElementSibling) && !element.nextElementSibling.classList.contains("show")) {
								const parents = e.getParents(element, '.dropdown-menu');
								e.removeClass(parents.querySelector('.show'), "show");
								if(e.isVariableDefined(parents.querySelector('.dropdown-opened'))){
										e.removeClass(parents.querySelector('.dropdown-opened'), "dropdown-opened");
								}
						}
						var $subMenu = e.getNextSiblings(element, ".dropdown-menu");
						e.toggleClass($subMenu, "show");
						$subMenu.previousElementSibling.toggleClass('dropdown-opened');
						var parents = e.getParents(element, 'li.nav-item.dropdown.show');
						if (e.isVariableDefined(parents) && parents.length > 0) {
								e.on(parents, 'hidden.bs.dropdown', function (event) {
										e.removeAllClass('.dropdown-submenu .show');
								});
						}
				});
		},
		// END: Navbar dropdown hover

  	// START: 03 Tiny Slider
		tinySlider: function () {
				var $carousel = e.select('.tiny-slider-inner');
				if (e.isVariableDefined($carousel)) {
					var tnsCarousel = e.selectAll('.tiny-slider-inner');
					tnsCarousel.forEach(slider => {
							var slider1 = slider;
							var sliderMode = slider1.getAttribute('data-mode') ? slider1.getAttribute('data-mode') : 'carousel';
							var sliderAxis = slider1.getAttribute('data-axis') ? slider1.getAttribute('data-axis') : 'horizontal';
							var sliderSpace = slider1.getAttribute('data-gutter') ? slider1.getAttribute('data-gutter') : 30;
							var sliderEdge = slider1.getAttribute('data-edge') ? slider1.getAttribute('data-edge') : 0;

							var sliderItems = slider1.getAttribute('data-items') ? slider1.getAttribute('data-items') : 4; //option: number (items in all device)
							var sliderItemsXl = slider1.getAttribute('data-items-xl') ? slider1.getAttribute('data-items-xl') : Number(sliderItems); //option: number (items in 1200 to end )
							var sliderItemsLg = slider1.getAttribute('data-items-lg') ? slider1.getAttribute('data-items-lg') : Number(sliderItemsXl); //option: number (items in 992 to 1199 )
							var sliderItemsMd = slider1.getAttribute('data-items-md') ? slider1.getAttribute('data-items-md') : Number(sliderItemsLg); //option: number (items in 768 to 991 )
							var sliderItemsSm = slider1.getAttribute('data-items-sm') ? slider1.getAttribute('data-items-sm') : Number(sliderItemsMd); //option: number (items in 576 to 767 )
							var sliderItemsXs = slider1.getAttribute('data-items-xs') ? slider1.getAttribute('data-items-xs') : Number(sliderItemsSm); //option: number (items in start to 575 )

							var sliderSpeed = slider1.getAttribute('data-speed') ? slider1.getAttribute('data-speed') : 500;
							var sliderautoWidth = slider1.getAttribute('data-autowidth') === 'true'; //option: true or false
							var sliderArrow = slider1.getAttribute('data-arrow') !== 'false'; //option: true or false
							var sliderDots = slider1.getAttribute('data-dots') !== 'false'; //option: true or false

							var sliderAutoPlay = slider1.getAttribute('data-autoplay') !== 'false'; //option: true or false
							var sliderAutoPlayTime = slider1.getAttribute('data-autoplaytime') ? slider1.getAttribute('data-autoplaytime') : 4000;
							var sliderHoverPause = slider1.getAttribute('data-hoverpause') === 'true'; //option: true or false
							if (e.isVariableDefined(e.select('.custom-thumb'))) {
								var sliderNavContainer = e.select('.custom-thumb');
							} 
							var sliderLoop = slider1.getAttribute('data-loop') !== 'false'; //option: true or false
							var sliderRewind = slider1.getAttribute('data-rewind') === 'true'; //option: true or false
							var sliderAutoHeight = slider1.getAttribute('data-autoheight') === 'true'; //option: true or false
							var sliderfixedWidth = slider1.getAttribute('data-fixedwidth') === 'true'; //option: true or false
							var sliderTouch = slider1.getAttribute('data-touch') !== 'false'; //option: true or false
							var sliderDrag = slider1.getAttribute('data-drag') !== 'false'; //option: true or false
							// Check if document DIR is RTL
							var ifRtl = document.getElementsByTagName("html")[0].getAttribute("dir");
							var sliderDirection;
							if (ifRtl === 'rtl') {
									sliderDirection = 'rtl';
							}

							var tnsSlider = tns({
									container: slider,
									mode: sliderMode,
									axis: sliderAxis,
									gutter: sliderSpace,
									edgePadding: sliderEdge,
									speed: sliderSpeed,
									autoWidth: sliderautoWidth,
									controls: sliderArrow,
									nav: sliderDots,
									autoplay: sliderAutoPlay,
									autoplayTimeout: sliderAutoPlayTime,
									autoplayHoverPause: sliderHoverPause,
									autoplayButton: false,
									autoplayButtonOutput: false,
									controlsPosition: top,
									navContainer: sliderNavContainer,
									navPosition: top,
									autoplayPosition: top,
									controlsText: [
											'<i class="fa-solid fa-chevron-left"></i>',
											'<i class="fa-solid fa-chevron-right"></i>'
									],
									loop: sliderLoop,
									rewind: sliderRewind,
									autoHeight: sliderAutoHeight,
									fixedWidth: sliderfixedWidth,
									touch: sliderTouch,
									mouseDrag: sliderDrag,
									arrowKeys: true,
									items: sliderItems,
									textDirection: sliderDirection,
									lazyload: true,
									lazyloadSelector: '.lazy',
									responsive: {
											0: {
													items: Number(sliderItemsXs)
											},
											576: {
													items: Number(sliderItemsSm)
											},
											768: {
													items: Number(sliderItemsMd)
											},
											992: {
													items: Number(sliderItemsLg)
											},
											1200: {
													items: Number(sliderItemsXl)
											}
									}
							});
					}); 
				}
		},
		// END: Tiny Slider


    // START: 04 Tooltip
		// Enable tooltips everywhere via data-toggle attribute
		toolTipFunc: function () {
				var tooltipTriggerList = [].slice.call(e.selectAll('[data-bs-toggle="tooltip"]'))
				var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
					return new bootstrap.Tooltip(tooltipTriggerEl)
				})
		},
		// END: Tooltip

		// START: 05 Popover
		// Enable popover everywhere via data-toggle attribute
		popOverFunc: function () {
				var popoverTriggerList = [].slice.call(e.selectAll('[data-bs-toggle="popover"]'))
				var popoverList = popoverTriggerList.map(function (popoverTriggerEl) {
					return new bootstrap.Popover(popoverTriggerEl)
				})
		},
		// END: Popover
    
    // START: 06 Video player
    videoPlyr: function () {
      var vdp = e.select('.player-wrapper');
      if (e.isVariableDefined(vdp)) {
        // youtube
        const playerYoutube = Plyr.setup('.player-youtube', {});
        window.player = playerYoutube;

        // Vimeo
        const playerVimeo = Plyr.setup('.player-vimeo', {});
        window.player = playerVimeo;
        
        // HTML video
        const playerHtmlvideo = Plyr.setup('.player-html', {
          captions: {active: true}
        });
        window.player = playerHtmlvideo;

        // HTML audio
        const playerHtmlaudio = Plyr.setup('.player-audio', {});
        window.player = playerHtmlaudio;
      }
    },
    // END: Video player

		// START: 07 GLightbox
		lightBox: function () {
				var light = e.select('[data-glightbox]');
				if (e.isVariableDefined(light)) {
						var lb = GLightbox({
								selector: '*[data-glightbox]',
								openEffect: 'fade',
								touchFollowAxis: 'true',
								closeEffect: 'fade'
						});
				}
		},
		// END: GLightbox

	// START: 08 Sidebar Toggle start
	sidebarToggleStart: function () {
		var sidebar = e.select('.sidebar-start-toggle');
		if (e.isVariableDefined(sidebar)) {
				var sb = e.select('.sidebar-start-toggle');
				var mode = document.getElementsByTagName("BODY")[0];
				sb.addEventListener("click", function(){
						mode.classList.toggle("sidebar-start-enabled");
				}); 
		}        
	},
	// END: Sidebar Toggle

	// START: 09 Sidebar Toggle end
	sidebarToggleEnd: function () {
		var sidebar = e.select('.sidebar-end-toggle');
		if (e.isVariableDefined(sidebar)) {
				var sb = e.select('.sidebar-end-toggle');
				var mode = document.getElementsByTagName("BODY")[0];
				sb.addEventListener("click", function(){
						mode.classList.toggle("sidebar-end-enabled");
				}); 
		}        
	},
	// END: Sidebar Toggle end

	// START: 10 Choices
	choicesSelect: function () {
		var choice = e.select('.js-choice');
		if (e.isVariableDefined(choice)) {
			var element = e.selectAll('.js-choice');
			element.forEach(function (item) {
				var removeItemBtn = item.getAttribute('data-remove-item-button') == 'true' ? true : false;
				var placeHolder = item.getAttribute('data-placeholder') == 'false' ? false : true;
				var placeHolderVal = item.getAttribute('data-placeholder-val') ? item.getAttribute('data-placeholder-val') : 'Type and hit enter';
				var maxItemCount = item.getAttribute('data-max-item-count') ? item.getAttribute('data-max-item-count') : 3;
				var searchEnabled = item.getAttribute('data-search-enabled') == 'true' ? true : false;
				var position = item.getAttribute('data-position') ? item.getAttribute('data-position') : 'auto';
				var choices = new Choices(item, {
						removeItemButton: removeItemBtn,
						placeholder: placeHolder,
						placeholderValue: placeHolderVal,
						maxItemCount: maxItemCount,
						searchEnabled: searchEnabled,
						position: position
				});
			});
		}
	},
	// END: Choices

	// START: 11 Auto resize textarea
	autoResize: function () {
		e.selectAll('[data-autoresize]').forEach(function (element) {
			var offset = element.offsetHeight - element.clientHeight;
			element.addEventListener('input', function (event) {
				event.target.style.height = 'auto';
				event.target.style.height = event.target.scrollHeight + offset + 'px';
			});
		});
	},
	// END: Auto resize textarea

	// START: 12 Drop Zone
	DropZone: function () {
		if (e.isVariableDefined(e.select("[data-dropzone]"))) {
			window.Dropzone.autoDiscover = false;

			// 1. Default Dropzone Initialization
			if (e.isVariableDefined(e.select(".dropzone-default"))) {
				e.selectAll(".dropzone-default").forEach((e => {
					const a = e.dataset.dropzone ? JSON.parse(e.dataset.dropzone) : {},
						b = {
							url: '/upload', // Change this URL to your actual image upload code
							// Fake the file upload, since GitHub does not handle file uploads
							// and returns a 404
							// https://docs.dropzone.dev/getting-started/setup/server-side-implementation
							init: function() {
								this.on('error', function(file, errorMessage) {
									if (file.accepted) {
										var mypreview = document.getElementsByClassName('dz-error');
										mypreview = mypreview[mypreview.length - 1];
										mypreview.classList.toggle('dz-error');
										mypreview.classList.toggle('dz-success');
									}
								});
							}
						},
						c = {
							...b,
							...a
						};
						new Dropzone(e, c);
					}));
			}
	
			// 2. Custom cover and list previews Dropzone Initialization
			if (e.isVariableDefined(e.select(".dropzone-custom"))) {
				e.selectAll(".dropzone-custom").forEach((d => {
					const j = d.dataset.dropzone ? JSON.parse(d.dataset.dropzone) : {},
						o = {
							addRemoveLinks: true,
							previewsContainer: d.querySelector(".dz-preview"),
							previewTemplate: d.querySelector(".dz-preview").innerHTML,
							url: '/upload', // Change this URL to your actual image upload code
							// Now fake the file upload, since GitHub does not handle file uploads
							// and returns a 404
							// https://docs.dropzone.dev/getting-started/setup/server-side-implementation
							init: function() {
								this.on('error', function(file, errorMessage) {
									if (file.accepted) {
										var mypreview = document.getElementsByClassName('dz-error');
										mypreview = mypreview[mypreview.length - 1];
										mypreview.classList.toggle('dz-error');
										mypreview.classList.toggle('dz-success');
									}
								});
							}
						},
						x = {
							...o,
							...j
						};
						d.querySelector(".dz-preview").innerHTML = '';
						new Dropzone(d, x);
				}));
			}
		}
	},
	// END: Drop Zone

	// START: 13 Flat picker
	flatPicker: function () {

    var picker = e.select('.flatpickr');
		if (e.isVariableDefined(picker)) {
			var element = e.selectAll('.flatpickr');
			element.forEach(function (item) {
				var mode = item.getAttribute('data-mode') == 'multiple' ? 'multiple' : item.getAttribute('data-mode') == 'range' ? 'range' : 'single';
				var enableTime = item.getAttribute('data-enableTime') == 'true' ? true : false;
				var noCalendar = item.getAttribute('data-noCalendar') == 'true' ? true : false;
				var inline = item.getAttribute('data-inline') == 'true' ? true : false;

				flatpickr(item, {
	        mode: mode,
	        enableTime: enableTime,
	        noCalendar: noCalendar,
	        inline: inline
	      });

			});
		}
  },
	// END: Flat picker

	// START: 14 Avatar Image
	avatarImg: function () {
		if (e.isVariableDefined(e.select('#avatarUpload'))) {
		
			var avtInput = e.select('#avatarUpload'),
			avtReset = e.select("#avatar-reset-img"),
			avtPreview = e.select('#avatar-preview');
		
			// Avatar upload and replace
			avtInput.addEventListener('change', readURL, true);
			function readURL(){
					const file = avtInput.files[0];
					const files = avtInput.files;
					const reader = new FileReader();
					reader.onloadend = function(){
							avtPreview.src = reader.result; 
					}
		
					if(file && files){
							reader.readAsDataURL(file);
					} else { }
		
					avtInput.value = '';
			}
		
			// Avatar remove functionality
			avtReset.addEventListener("click", function(){
				avtPreview.src = "/assets/images/avatar/placeholder.jpg";
			});
		}			
	},
	// END: Avatar Image

	// START: 15 Custom Scrollbar
	customScrollbar: function () {

		if (e.isVariableDefined(e.select(".custom-scrollbar"))) {
			document.addEventListener("DOMContentLoaded", function() {
				var instances = OverlayScrollbars(e.selectAll('.custom-scrollbar'), {
					resize : "none",
					scrollbars: {
						autoHide: 'leave',
						autoHideDelay: 200
					},
					overflowBehavior : {
							x : "visible-hidden",
							y : "scroll"
					}
				});
			});
		}
	
		if (e.isVariableDefined(e.select(".custom-scrollbar-y"))) {
			document.addEventListener("DOMContentLoaded", function() {
				var instances = OverlayScrollbars(e.selectAll('.custom-scrollbar-y'), {
					resize : "none",
					scrollbars: {
						autoHide: 'leave',
						autoHideDelay: 200
					},
					overflowBehavior : {
							x : "scroll",
							y : "scroll"
					}
				});
			});
		}	
	},
	// END: Custom Scrollbar

	// START: 16 Toasts
	toasts: function () {
		if (e.isVariableDefined(e.select('.toast-btn'))) {
			window.addEventListener('DOMContentLoaded', (event) => {
					e.selectAll(".toast-btn").forEach((t) => {
						t.addEventListener("click", function() {
							var toastTarget = document.getElementById(t.dataset.target);
							var toast = new bootstrap.Toast(toastTarget);
							toast.show();
						});
					});
			});
		}
	},
	// END: Toasts

	// START: 17 pswMeter
	pswMeter: function () {
		if (e.isVariableDefined(e.select('#pswmeter'))) {
			const myPassMeter = passwordStrengthMeter({
				containerElement: '#pswmeter',
				passwordInput: '#psw-input',
				showMessage: true,
				messageContainer: '#pswmeter-message',
				messagesList: [
					'Write your password...',
					'Easy peasy!',
					'That is a simple one',
					'That is better',
					'Yeah! that password rocks ;)'
				],
				height: 8,
				borderRadius: 4,
				pswMinLength: 8,
				colorScore1: '#dc3545',
				colorScore2: '#f7c32e',
				colorScore3: '#4f9ef8',
				colorScore4: '#0cbc87'
			});
		}
	},
  // END: pswMeter

	// START: 18 Fake Password
	fakePwd: function () {
		if (e.isVariableDefined(e.select('.fakepassword'))) {
			var password = e.select('.fakepassword');
			var toggler = e.select('.fakepasswordicon');
		
			var showHidePassword = () => {
				if (password.type == 'password') {
					password.setAttribute('type', 'text');
					toggler.classList.add('fa-eye');
				} else {
					toggler.classList.remove('fa-eye');
					password.setAttribute('type', 'password');
				}
			};
		
			toggler.addEventListener('click', showHidePassword);
		}
	}
  // END: Fake Password
 
};
e.init();
