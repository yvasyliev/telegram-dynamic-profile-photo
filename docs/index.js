redirectToBot = () => {
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    const bot = urlParams.get('bot');
    const code = urlParams.get('code');
    if (bot && code) {
        const url = new URL(bot, "https://t.me");
        url.searchParams.append("start", code);
        document.body.innerHTML = `<p>If you haven't been automatically redirected to Telegram bot, please follow the link: <a href="${url}">${url}</a></p>${document.body.innerHTML}`;
        window.location.replace(url);
    }
}