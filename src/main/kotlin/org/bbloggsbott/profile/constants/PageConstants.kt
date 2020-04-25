package org.bbloggsbott.profile.constants

val PAGEMETA = "pagemeta"
val PAGETITLE = "title"
val PAGECOLLECTIONS = "collections"
val PAGEPERMALINK = "permalink"
val PAGEEXCERPT = "excerpt"
val PAGEDATE = "date"
val PAGEVENUE = "venue"
val PAGEPAPERURL = "paperurl"

val NECESSARYPAGEKEYS = listOf<String>(PAGETITLE, PAGECOLLECTIONS, PAGEPERMALINK, PAGEEXCERPT, PAGEDATE, PAGEVENUE, PAGEPAPERURL)

val PAGE_META_REGEX = Regex("---\n(.*\n)+---")
