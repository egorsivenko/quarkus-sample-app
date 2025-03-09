@import io.micronaut.data.model.Page

@param String path
@param Page<?> page
@param String emptyContentMessage

<caption>
    @if(page.isEmpty())
        <p>${emptyContentMessage}</p>
    @elseif(page.getTotalPages() > 1)
        Page:
        @for(int pageNumber = 0; pageNumber < page.getTotalPages(); pageNumber++)
            @if(page.getPageNumber() == pageNumber)
                ${pageNumber + 1}
            @else
                <a href="${path}?page=${pageNumber}&size=${page.getSize()}">${pageNumber + 1}</a>
            @endif
        @endfor
    @endif
</caption>