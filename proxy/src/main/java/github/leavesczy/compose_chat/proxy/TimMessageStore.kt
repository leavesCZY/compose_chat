package github.leavesczy.compose_chat.proxy

import com.tencent.imsdk.v2.V2TIMMessage

internal object TimMessageStore {

    private const val MAX_SIZE = 1000

    private val lock = Any()

    private val messageMap = object : LinkedHashMap<String, V2TIMMessage>(64, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, V2TIMMessage>?): Boolean {
            return size > MAX_SIZE
        }
    }

    private val aliasMap = object : LinkedHashMap<String, String>(64, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, String>?): Boolean {
            return size > MAX_SIZE
        }
    }

    fun put(msgId: String, timMessage: V2TIMMessage) {
        if (msgId.isBlank()) {
            return
        }
        synchronized(lock) {
            messageMap[msgId] = timMessage
        }
    }

    fun putAlias(localMsgId: String, remoteMsgId: String) {
        if (localMsgId.isBlank() || remoteMsgId.isBlank() || localMsgId == remoteMsgId) {
            return
        }
        synchronized(lock) {
            aliasMap[localMsgId] = remoteMsgId
        }
    }

    fun get(msgId: String): V2TIMMessage? {
        if (msgId.isBlank()) {
            return null
        }
        synchronized(lock) {
            messageMap[msgId]?.let { return it }
            val aliasId = aliasMap[msgId] ?: return null
            return messageMap[aliasId]
        }
    }

    fun remove(msgId: String) {
        if (msgId.isBlank()) {
            return
        }
        synchronized(lock) {
            messageMap.remove(key = msgId)
            aliasMap.remove(key = msgId)
            aliasMap.entries.removeAll { entry ->
                entry.value == msgId
            }
        }
    }

    fun clear() {
        synchronized(lock) {
            messageMap.clear()
            aliasMap.clear()
        }
    }

}
