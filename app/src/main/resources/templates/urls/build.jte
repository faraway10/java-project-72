@import hexlet.code.dto.BasePage
@import hexlet.code.util.NamedRoutes
@param BasePage page

@template.layout.page(
page = page,
content = @`
    <form action="${NamedRoutes.urlsPath()}" method="post">
        Сайт
        <input type="text" required name="url">
        <input type="submit">
    </form>
`
)