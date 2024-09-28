@import hexlet.code.dto.BasePage
@import hexlet.code.util.NamedRoutes
@param BasePage page

@template.layout.page(
page = page,
content = @`
    <form action="${NamedRoutes.urlsPath()}" method="post" class="rss-form text-body">
        <div class="row">
            <div class="col">
                <div class="form-floating">
                    <input id="url-input" autofocus="" type="text" required="" name="url" aria-label="url" class="form-control w-100" placeholder="ссылка" autocomplete="off">
                    <label for="url-input">Ссылка</label>
                </div>
            </div>
            <div class="col-auto">
                <button type="submit" class="h-100 btn btn-lg btn-primary px-sm-5">Проверить</button>
            </div>
        </div>
    </form>
`
)