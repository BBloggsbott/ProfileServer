package org.bbloggsbott.profile.pages.service

import org.bbloggsbott.profile.application.service.PropertyService
import org.bbloggsbott.profile.constants.*
import org.bbloggsbott.profile.pages.dto.PageDTO
import org.commonmark.node.Node
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Service
class MarkdownService {

    @Autowired
    lateinit var propertyService: PropertyService

    val parser = Parser.builder().build()
    val htmlRenderer = HtmlRenderer.builder().build()
    val logger = LoggerFactory.getLogger(this.javaClass)

    fun setPageDTO(pageDTO: PageDTO){
        logger.info("Starting Markdown Meta setting and HTML generation")
        logger.info("Starting markdown parsing")
        var node = parser.parse(pageDTO.contentMD)
        if (node.firstChild == null || node.firstChild.next == null){
            setAllMetaToDefault(pageDTO)
            pageDTO.contentHTML = htmlRenderer.render(parser.parse(pageDTO.contentMD))
            return
        }
        var child = node.firstChild
        val metaNode = child.next
        logger.info("Extracting Meta")
        var metaNodeContent = htmlRenderer.render(metaNode).replace(Regex("</?h2>"), "").split("\n")
        val metaMaps = HashMap<String, String?>()
        if (metaNodeContent.get(0) == PAGEMETA){
            logger.info("Meta exists. Setting meta")
            for (i in 1..(metaNodeContent.size-1)){
                var content = metaNodeContent.get(i)
                var titleParts = content.split(":")
                if (titleParts.size >=2){
                    var key = titleParts[0].trim()
                    var  value = titleParts[1]
                    metaMaps[key.trim()] = value.trim()
                } else if (titleParts.size == 1){
                    metaMaps[titleParts[0]] = null
                }
            }
            for (key in NECESSARYPAGEKEYS){
                if (key in metaMaps.keys){
                    setPageDTOKey(pageDTO, key, metaMaps[key]!!)
                } else {
                    logger.info("Necessary key $key not found in ${pageDTO.pagePath}. Using Default")
                    setDefaultPageMeta(pageDTO, key)
                }
            }
            logger.info("Removing meta from markdown")
            pageDTO.contentMD = removePageMeta(pageDTO.contentMD!!)
        } else {
            logger.info("Meta not found. Using defaults")
            setAllMetaToDefault(pageDTO)
        }
        logger.info("Creating html for content")
        pageDTO.contentHTML = htmlRenderer.render(parser.parse(pageDTO.contentMD))
        logger.info("PageDTO creation complete.")
    }

    fun setPageDTOKey(pageDTO: PageDTO, key: String, value: String){
        when(key){
            PAGETITLE -> pageDTO.title = value
            PAGECOLLECTIONS -> {
                var collectionsList = ArrayList<String>()
                value.split(",").forEach { collectionsList.add(it.trim()) }
                pageDTO.pageCollections = collectionsList
            }
            PAGEPERMALINK -> pageDTO.permalink = value.trim()
            PAGEEXCERPT -> pageDTO.excerpt = value.trim()
            PAGEDATE -> {
                val formatter = SimpleDateFormat(propertyService.getProperties().dateTimeFormat!!)
                pageDTO.date = formatter.parse(value.trim())
            }
            PAGEVENUE -> pageDTO.pageVenue = value.trim()
            PAGEPAPERURL -> pageDTO.paperUrl = value.trim()
        }
    }

    fun setDefaultPageMeta(pageDTO: PageDTO, key: String){
        when(key){
            PAGETITLE -> pageDTO.title = pageDTO.pagePath.split("/").last()
            PAGECOLLECTIONS -> pageDTO.pageCollections = listOf()
            PAGEPERMALINK -> pageDTO.permalink = pageDTO.pagePath.split(propertyService.getProperties().pagesDirectory!!).last()
            PAGEEXCERPT -> pageDTO.excerpt = null
            PAGEDATE -> pageDTO.date = Date()
            PAGEVENUE -> pageDTO.pageVenue = null
            PAGEPAPERURL -> pageDTO.paperUrl = null
        }
    }

    fun setAllMetaToDefault(pageDTO: PageDTO){
        for (key in NECESSARYPAGEKEYS){
            setDefaultPageMeta(pageDTO, key)
        }
    }

    fun removePageMeta(contentMD: String): String{
        return contentMD.replaceFirst(PAGE_META_REGEX, "").trim()
    }

}