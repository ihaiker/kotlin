package la.renzhen.kotlin.load

/**
 * <p>
 * @author <a href="mailto:zhouhaichao@2008.sina.com">haiker</a>
 * @version 02/09/2018 9:15 PM
 */

typealias IteratorLoader<Index, Out> = (Index?) -> MutableList<Out>

typealias IteratorIndex<Out, Index> = (Out) -> Index

/**
 * 数据加载迭代器
 * @param firstIndex 第一次加载index
 * @param indexFn index更新器
 * @param loader 数据加载器
 */
open class IndexIterator<Index, Out>
constructor(
        private val firstIndex: Index? = null,
        private val indexFn: IteratorIndex<Out, Index>? = null,
        private val loader: IteratorLoader<Index?, Out>
) : Iterator<Out> {

    protected var data: MutableList<Out> = mutableListOf();
    var index: Index? = firstIndex

    override fun hasNext(): Boolean {
        if (data.isEmpty()) {
            preLoad(index)
            data = loader.invoke(index)
            afterLoaded(data)
            if (data.isEmpty()) {
                return false
            }
        }
        return !data.isEmpty()
    }

    /**
     * 加载数据之后操作
     */
    open protected fun afterLoaded(data: MutableList<Out>) {

    }

    /**
     * 加载数据之前
     * @param index 当前索引
     */
    open protected fun preLoad(index: Index?) {

    }

    override fun next(): Out {
        val out = data.removeAt(0)
        indexFn?.let {
            index = it.invoke(out)
        }
        return out
    }

}

open class PageIndex<Out> constructor(
        private var firstPage: Int = 1,
        private val loader: IteratorLoader<Int, Out>
) : IndexIterator<Int, Out>(firstIndex = firstPage, loader = loader) {

    override fun afterLoaded(data: MutableList<Out>) {
        this.index = this.index!! + 1
    }
}