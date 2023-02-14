using FamilLinkProject.Core.domain;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.CompilerServices;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Automation.Peers;

namespace FamilLinkProject.Core
{
    public class FaceDectionData
    {
        // 모을 얼굴 인식 최대 개수
        private const int MAX_SIZE = 5;

        // 인식 기준
        private const int COGNIZE_THRESHOLD = 4;

        // 로그아웃 기준
        private const int NONE_THRESHOLD = 5;

        private static Dictionary<string, long> reverseMembers = new Dictionary<string, long>();

        private static List<MemberDTO> members = new List<MemberDTO>();
        private static List<string> list = new List<string>();

        public static Dictionary<string, long> getReverseMembers()
        {
            return reverseMembers;
        }

        public static void setMembers(List<MemberDTO> _members)
        {
            reverseMembers.Clear();
            members = _members;

            foreach(var item in members)
            {
                reverseMembers.Add(item.Name, item.Uid);
            }
        }


        public static void add(string name)
        {
            list.Add(name);
            if (list.Count > MAX_SIZE)
            {
                list.RemoveAt(0);
            }
        }

        public static long getDetectionUid()
        {
            int noneCount = list.Where((item) =>
            {
                return item.Equals("NONE");
            }).ToList().Count;
            if (noneCount >= NONE_THRESHOLD)
                return -1;

            foreach (var item in members) {
                var result = list.Where(x => x.Equals(item.Name)).ToList().Count;
                if(result >= COGNIZE_THRESHOLD)
                {
                    return item.Uid;
                }
            }

            return 0;

        }

    }
}
