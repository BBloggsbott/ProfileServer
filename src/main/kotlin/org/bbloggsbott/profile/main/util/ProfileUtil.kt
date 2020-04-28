package org.bbloggsbott.profile.main.util

import org.bbloggsbott.profile.main.dto.ProfileDTO
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ProfileUtil {

    @Value("\${github.baseurl}")
    private lateinit var githubBaseUrl: String

    @Value("\${gitlab.baseurl}")
    private lateinit var gitlabBaseUrl: String

    @Value("\${linkedin.profile.baseurl}")
    private lateinit var linkedinBaseUrl: String

    @Value("\${twitter.baseurl}")
    private lateinit var twitterBaseUrl: String

    private fun getGitHubUrlFromUsername(username: String) = githubBaseUrl + username.trim()

    private fun getGitLabUrlFromUsername(username: String) = gitlabBaseUrl + username.trim()

    private fun getLinkedinUrlFromUsername(username: String) = linkedinBaseUrl + username.trim()

    private fun getTwitterUrlFromUsername(username: String) = twitterBaseUrl + username.trim()

    fun prepareSocialMediaUrls(profileDTO: ProfileDTO){
        var socialMedia = profileDTO.socialMedia
        socialMedia.github = getGitHubUrlFromUsername(socialMedia.github)
        socialMedia.gitlab = getGitLabUrlFromUsername(socialMedia.gitlab)
        socialMedia.linkedin = getLinkedinUrlFromUsername(socialMedia.linkedin)
        socialMedia.twitter = getTwitterUrlFromUsername(socialMedia.twitter)
    }

}