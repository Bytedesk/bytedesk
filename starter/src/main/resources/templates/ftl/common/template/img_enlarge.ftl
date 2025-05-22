<!--黑色遮罩-->
<div class="kb_black_overlay" id="kb_black_overlay"></div>
<!--预览容器，存放点击放大后的图片-->
<div class="kb_enlarge_container" id="kb_enlarge_container">
    <!-- 关闭按钮，一个叉号图片 -->
    <img src="/kbase/img/close.png" alt="close" class="kb_close" id="kb_close">
</div>

<script>
    let kb_black_overlay = document.getElementById('kb_black_overlay');
    let kb_enlarge_container = document.getElementById('kb_enlarge_container');
    let kb_close_btn = document.getElementById('kb_close');
    //
    var images = document.images
    for (const myimg of images) {
        myimg.classList.add('kb_img')
        myimg.addEventListener('click', function () {
            // 获取当前图片的路径
            let imgUrl = this.src;
            // 显示黑色遮罩和预览容器
            kb_black_overlay.style.display = 'block';
            kb_enlarge_container.style.display = 'block';
            let img = new Image();
            img.src = imgUrl;
            img.classList.add('kb_enlarge_preview_img');
            if (kb_close_btn.nextElementSibling) {
                kb_enlarge_container.removeChild(kb_close_btn.nextElementSibling);
            }
            kb_enlarge_container.appendChild(img);
        });
    }

    kb_black_overlay.addEventListener('click', function () {
        kb_black_overlay.style.display = 'none';
        kb_enlarge_container.style.display = 'none';
    });

    // 关闭预览
    kb_close_btn.addEventListener('click', function () {
        kb_black_overlay.style.display = 'none';
        kb_enlarge_container.style.display = 'none';
    });
</script>